package com.smart.future.dao.video;

import com.smart.future.video.vo.FilmVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IFilmDao {
    List<FilmVO> query(@Param("startRow") Integer startRow, @Param("size") Integer size);

    void create(@Param("filmVO") FilmVO filmVO);

    FilmVO queryById(@Param("id") Long id);
}
