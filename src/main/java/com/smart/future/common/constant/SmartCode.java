package com.smart.future.common.constant;

public interface SmartCode {
    Integer OK = 200;

    interface LoginError{
        Integer AUTH_ERROR = 1000;
    }

    interface UserError{
        Integer EMPTY_PARAM = 2010;
        Integer ERROR_PARAM = 2011;
    }
}
