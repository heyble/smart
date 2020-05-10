package com.smart.future.storage.service.impl;

import com.smart.future.common.constant.ResourceCode;
import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.constant.SymbolCode;
import com.smart.future.common.exception.ApplicationException;
import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.common.util.FileHashUtil;
import com.smart.future.dao.file.IFileDao;
import com.smart.future.storage.service.IStorageService;
import com.smart.future.storage.vo.ChunkVO;
import com.smart.future.storage.vo.FileVO;
import com.smart.future.user.service.IUserService;
import com.smart.future.user.vo.UserVO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class StorageServiceImpl implements IStorageService {

    @Value("${upload.temp-path}")
    private String rootFilePathTemp;
    @Value("${upload.real-path}")
    private String rootFilePath;
    @Autowired
    private IFileDao fileDao;
    @Autowired
    private IUserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);

    @Override
    public void uploadFile2Temp(ChunkVO chunk) throws ApplicationException {
        MultipartFile file = chunk.getFile();
        if (file == null) {
            LOGGER.warn("ChunkVO中file为空");
            throw new SmartApplicationException(SmartCode.Storage.EMPTY_PARAM, "文件为空");
        }

        Integer chunkNumber = chunk.getChunkNumber();
        if (chunkNumber == null) {
            chunkNumber = 0;
        }

        File outFile = new File(rootFilePathTemp + File.separator + chunk.getFileName(), chunkNumber + ".part");

        try {
            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, outFile);
        } catch (IOException e) {
            LOGGER.error("拷贝InputStream到文件出错", e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
    }

    @Override
    public FileVO mergeFile4Temp(ChunkVO chunk) throws ApplicationException {
        String suffixName = getSuffixName(chunk.getFileName());
        final String resourceCode = ResourceCode.getResourceCode(suffixName);
        String newFileName = System.currentTimeMillis()+SymbolCode.DOT+suffixName;
        final String basePath = buildBasePath(resourceCode, newFileName);
        Long size = 0L;
        try {
            File file = new File(rootFilePathTemp + File.separator + chunk.getFileName());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    File partFile = new File(rootFilePath + basePath);
                    // 创建文件夹
                    if (!partFile.getParentFile().exists()) {
                        partFile.getParentFile().mkdirs();
                    }
                    for (int i = 0; i < files.length; i++) {
                        File s = new File(rootFilePathTemp + File.separator + chunk.getFileName(), i + ".part");
                        FileOutputStream destTempfos = new FileOutputStream(partFile, true);
                        FileUtils.copyFile(s, destTempfos);
                        destTempfos.close();
                    }
                    FileUtils.deleteDirectory(file);
                    size = partFile.length();
                }
            }
        } catch (IOException e) {
            LOGGER.error("合并临时文件出错", e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
        // 写入数据库
        final FileVO fileVO = new FileVO();
        fileVO.setName(chunk.getFileName());
        fileVO.setSuffixName(suffixName);
        fileVO.setOwner(0L);
        fileVO.setResourceCode(resourceCode);
        fileVO.setPath(basePath);
        fileVO.setSize(size);
        fileVO.setHashCode("");
        fileVO.setAvailable(1);
        fileVO.setCreatedBy(0L);
        fileVO.setCreationDate(new Date());
        fileVO.setLastUpdatedBy(0L);
        fileVO.setLastUpdatedDate(new Date());
        fileDao.createFile(fileVO);
        return fileVO;
    }

    private String buildBasePath(String resourceCode, String newFileName) {
        return File.separator + resourceCode + File.separator + newFileName;
    }

    private String getSuffixName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    @Override
    public File loadFile(String basePath) throws ApplicationException {
        final File file = new File(rootFilePath + File.separator + basePath);
        if (!file.exists()) {
            throw new SmartApplicationException(SmartCode.Storage.NOT_FOUND, "文件不存在");
        }
        return file;
    }

    @Override
    public FileVO upload(MultipartFile multipartFile) throws SmartApplicationException {
        final String originalFilename = multipartFile.getOriginalFilename();
        final Object phoneId = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserVO user = userService.findUserByPhoneId(Long.parseLong(phoneId.toString()));
        try {
            final String hash = FileHashUtil.getSHA256(multipartFile.getInputStream());
            FileVO fileVO = fileDao.queryByHashCode(hash);
            if (fileVO != null) {
                fileVO.setName(originalFilename);
                fileVO.setOwner(user.getId());
                fileVO.setAvailable(1);
                fileVO.setCreatedBy(user.getId());
                fileVO.setCreationDate(new Date());
                fileVO.setLastUpdatedBy(user.getId());
                fileVO.setLastUpdatedDate(new Date());
            } else {
                final String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                final String resourceCode = ResourceCode.getResourceCode(suffixName);
                final String relativePath = generateRelativePath(suffixName, resourceCode);
                final File file = new File(rootFilePath, relativePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                multipartFile.transferTo(file);
                fileVO = new FileVO();
                fileVO.setName(originalFilename);
                fileVO.setHashCode(hash);
                fileVO.setPath(relativePath);
                fileVO.setResourceCode(resourceCode);
                fileVO.setOwner(user.getId());
                fileVO.setSuffixName(suffixName);
                fileVO.setSize(Long.parseLong(multipartFile.getSize() + ""));
                fileVO.setAvailable(1);
                fileVO.setCreatedBy(user.getId());
                fileVO.setCreationDate(new Date());
                fileVO.setLastUpdatedBy(user.getId());
                fileVO.setLastUpdatedDate(new Date());
            }
            fileDao.createFile(fileVO);
            return fileVO;
        } catch (SmartApplicationException | IOException e) {
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, e.getMessage());
        }
    }

    private String generateRelativePath(String suffixName, String resourceCode) {
        return SymbolCode.SLASH + resourceCode + SymbolCode.SLASH + System.currentTimeMillis() + SymbolCode.DOT + suffixName;
    }
}
