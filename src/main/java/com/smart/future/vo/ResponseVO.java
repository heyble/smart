package com.smart.future.vo;

public class ResponseVO<T> {
    Integer status;
    String message;
    T data;

    public ResponseVO() {
    }

    public ResponseVO(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseVO(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ResponseVO<Void> ok(){
        return new ResponseVO<Void>(200, "success");
    }

    public static <E> ResponseVO<E> okWithData(E data){
        return new ResponseVO<E>(200, "success", data);
    }

    public static ResponseVO<Void> error(Integer status, String message){
        return new ResponseVO<>(status, message);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
