package com.smart.future.user.controller;

import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.exception.ApplicationException;
import com.smart.future.user.service.IUserService;
import com.smart.future.user.vo.UserVO;
import com.smart.future.common.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/register")
    public ResponseVO<Void> addUser(@RequestBody UserVO userVO){
        try {
            userService.addUser(userVO);
        } catch (ApplicationException e) {
            ResponseVO.error(SmartCode.UserError.EMPTY_PARAM, e.getMessage());
        }
        return ResponseVO.ok();
    }

    @RequestMapping("/update")
    public ResponseVO<UserVO> updateUser(UserVO userVO){
        UserVO user = userService.updateUser(userVO);
        return ResponseVO.okWithData(user);
    }

    @RequestMapping("/findAll")
    public ResponseVO<List<UserVO>> findALl(){
        List<UserVO> users = userService.findAll();
        return ResponseVO.okWithData(users);
    }
}
