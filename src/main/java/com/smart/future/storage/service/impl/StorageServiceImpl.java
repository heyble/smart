package com.smart.future.storage.service.impl;

import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.exception.ApplicationException;
import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.storage.service.IStorageService;
import com.smart.future.storage.vo.ChunkVO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class StorageServiceImpl implements IStorageService {

    @Value("${upload.temp-path}")
    private String filePathTemp;
    @Value("${upload.real-path}")
    private String filePath;
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

        File outFile = new File(filePathTemp + File.separator + chunk.getIdentifier(), chunkNumber + ".part");

        try {
            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, outFile);
        } catch (IOException e) {
            LOGGER.error("拷贝InputStream到文件出错",e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
    }

    @Override
    public void mergeFile4Temp(String filename, String guid) throws ApplicationException {
        try {
            File file = new File(filePathTemp + File.separator + guid);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    File partFile = new File(filePath + File.separator + filename);
                    for (int i = 1; i <= files.length; i++) {
                        File s = new File(filePathTemp + File.separator + guid, i + ".part");
                        FileOutputStream destTempfos = new FileOutputStream(partFile, true);
                        FileUtils.copyFile(s, destTempfos);
                        destTempfos.close();
                    }
                    FileUtils.deleteDirectory(file);
                }
            }
        } catch (IOException e) {
            LOGGER.error("合并临时文件出错",e);
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
    }

    @Override
    public File loadFile(String basePath) throws ApplicationException {
        final File file = new File(filePath + File.separator + basePath);
        if (!file.exists()) {
            throw new SmartApplicationException(SmartCode.Storage.NOT_FOUND, "文件不存在");
        }
        return file;
    }
}
