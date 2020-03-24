package com.smart.future.common.exception;

public abstract class ApplicationException extends Exception {

    protected Integer code;

    public ApplicationException() {
    }

    public ApplicationException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return code + ": "+super.getMessage();
    }

    public String getErrorMessage(){
        return super.getMessage();
    }
}
