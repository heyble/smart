package com.smart.future.common.exception;

public class SmartApplicationException extends ApplicationException {

    public SmartApplicationException() {
    }

    public SmartApplicationException(Integer code ,String message) {
        super(message);
        this.code = code;
    }
}
