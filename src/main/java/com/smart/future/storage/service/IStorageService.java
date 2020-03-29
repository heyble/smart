package com.smart.future.storage.service;

import com.smart.future.common.exception.ApplicationException;
import com.smart.future.storage.vo.ChunkVO;

import java.io.File;

public interface IStorageService {

    void uploadFile2Temp(ChunkVO chunk) throws ApplicationException;

    void mergeFile4Temp(String filename, String guid) throws ApplicationException;

    File loadFile(String filePath) throws ApplicationException;
}
