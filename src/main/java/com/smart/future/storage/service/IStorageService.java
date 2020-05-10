package com.smart.future.storage.service;

import com.smart.future.common.exception.ApplicationException;
import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.storage.vo.ChunkVO;
import com.smart.future.storage.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IStorageService {

    void uploadFile2Temp(ChunkVO chunk) throws ApplicationException;

    FileVO mergeFile4Temp(ChunkVO chunk) throws ApplicationException;

    File loadFile(String filePath) throws ApplicationException;

    FileVO upload(MultipartFile multipartFile) throws SmartApplicationException;
}
