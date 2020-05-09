package com.smart.future.video.service.impl;

import com.smart.future.dao.video.IFilmDao;
import com.smart.future.user.service.IUserService;
import com.smart.future.user.vo.UserVO;
import com.smart.future.video.service.IFilmService;
import com.smart.future.video.vo.FilmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FilmService implements IFilmService {

    @Autowired
    private IFilmDao filmDao;

    @Autowired
    private IUserService userService;

    @Override
    public List<FilmVO> query(Integer currentPage, Integer size) {

        return filmDao.query((currentPage-1)*size,size);
    }

    @Override
    public void create(FilmVO filmVO) {
        filmVO.setAvailable(1);
        final Long phoneId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        final UserVO user = userService.findUserByPhoneId(phoneId);
        filmVO.setCreatedBy(user.getId());
        filmVO.setCreationDate(new Date());
        filmVO.setLastUpdatedBy(user.getId());
        filmVO.setLastUpdatedDate(new Date());
        filmDao.create(filmVO);
    }

    @Override
    public FilmVO queryById(Long id) {

        return filmDao.queryById(id);
    }

    @Override
    public void update(FilmVO filmVO) {
        filmVO.setLastUpdatedDate(new Date());
        filmDao.update(filmVO);
    }
}
