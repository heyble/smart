package com.smart.future.video.service;

import com.smart.future.video.vo.FilmVO;

import java.util.List;

public interface IFilmService {
    List<FilmVO> query(Integer currentPage, Integer size);

    void create(FilmVO filmVO);

    FilmVO queryById(Long id);

    void update(FilmVO filmVO);
}
