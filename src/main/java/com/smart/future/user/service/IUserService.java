package com.smart.future.user.service;

import com.smart.future.common.exception.ApplicationException;
import com.smart.future.user.vo.UserVO;

import java.util.List;

public interface IUserService {

    void addUser(UserVO userVO) throws ApplicationException;

    List<UserVO> findAll();

    UserVO updateUser(UserVO userVO);

    UserVO findUserByPhoneId(Long phoneId);
}
