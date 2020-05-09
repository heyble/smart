package com.smart.future.video.controller;

import com.smart.future.common.vo.ResponseVO;
import com.smart.future.video.service.IFilmService;
import com.smart.future.video.vo.FilmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/film")
public class FilmController {

    @Autowired
    private IFilmService filmService;

    @RequestMapping("/get/{currentPage}/{size}")
    public ResponseVO<?> query(@PathVariable Integer currentPage, @PathVariable Integer size){
        List<FilmVO> films = filmService.query(currentPage,size);
        return ResponseVO.okWithData(films);
    }

    @RequestMapping("/create")
    public ResponseVO<?> create(@RequestBody FilmVO filmVO){
        filmService.create(filmVO);
        return ResponseVO.ok();
    }

    @RequestMapping("/{id}")
    public ResponseVO<?> query(@PathVariable Long id){
        FilmVO filmVO = filmService.queryById(id);
        return ResponseVO.okWithData(filmVO);
    }
}
