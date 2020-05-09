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
import org.springframework.util.ResourceUtils;
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
            chunkNumber = 1;
        }

        File outFile = new File(rootFilePathTemp + File.separator + chunk.getIdentifier(), chunkNumber + ".part");

        try {
            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, outFile);
        } catch (IOException e) {
            LOGGER.error("拷贝InputStream到文件出错", e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
    }

    @Override
    public void mergeFile4Temp(String filename, String guid) throws ApplicationException {
        try {
            File file = new File(rootFilePathTemp + File.separator + guid);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    File partFile = new File(rootFilePath + File.separator + filename);
                    for (int i = 1; i <= files.length; i++) {
                        File s = new File(rootFilePathTemp + File.separator + guid, i + ".part");
                        FileOutputStream destTempfos = new FileOutputStream(partFile, true);
                        FileUtils.copyFile(s, destTempfos);
                        destTempfos.close();
                    }
                    FileUtils.deleteDirectory(file);
                }
            }
        } catch (IOException e) {
            LOGGER.error("合并临时文件出错", e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
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
                File filePath = new File(ResourceUtils.getURL("classpath:").getPath());
                // File upload = new File(filePath.getAbsolutePath(), "static/tmpupload/");
                String path = filePath.getAbsolutePath() + "/static" + relativePath;
                final File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                multipartFile.transferTo(new File(path));
                fileVO = new FileVO();
                fileVO.setName(originalFilename);
                fileVO.setHashCode(hash);
                fileVO.setPath(relativePath);
                fileVO.setResourceCode(resourceCode);
                fileVO.setOwner(user.getId());
                fileVO.setSuffixName(suffixName);
                fileVO.setSize(Integer.parseInt(multipartFile.getSize() + ""));
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


        // final FileVO fileVO = new FileVO();
        // fileVO.setName(originalFilename);

    }

    private String generateRelativePath(String suffixName, String resourceCode) {
        return SymbolCode.SLASH + resourceCode + SymbolCode.SLASH + System.currentTimeMillis() + SymbolCode.DOT + suffixName;
    }
}
