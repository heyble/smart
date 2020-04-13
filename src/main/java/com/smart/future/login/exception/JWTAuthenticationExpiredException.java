package com.smart.future.login.exception;

import com.smart.future.common.exception.ApplicationException;

public class JWTAuthenticationExpiredException extends ApplicationException {
    public JWTAuthenticationExpiredException() {
    }

    public JWTAuthenticationExpiredException(Integer code ,String message) {
        super(message);
        this.code = code;
    }
}
