package com.smart.future.dao.file;

import com.smart.future.storage.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IFileDao {

    FileVO queryByHashCode(@Param("hashCode") String hashCode);

    void createFile(@Param("fileVO") FileVO fileVO);
}
