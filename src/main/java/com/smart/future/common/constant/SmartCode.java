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

    interface Storage{
        Integer EMPTY_PARAM = 3010;
        Integer ERROR_PARAM = 3011;
        Integer REQUEST_ERROR = 3020;
        Integer INTERNAL_ERROR = 500;
        Integer NOT_FOUND = 4010;
    }
}
