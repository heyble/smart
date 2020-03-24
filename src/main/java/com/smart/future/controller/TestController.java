package com.smart.future.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/health")
    public Object health(){
        return "I'm OK!";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Object login(){
        return "login success!";
    }
}
