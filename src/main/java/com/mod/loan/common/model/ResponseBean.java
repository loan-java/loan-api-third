package com.mod.loan.common.model;


public class ResponseBean<T> {

    private int code;
    private String msg;
    private T data;

    public ResponseBean() {
    }

    public ResponseBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseBean<T> success(T data) {
        return new ResponseBean<>(200, "成功", data);
    }

    public static <T> ResponseBean<T> fail(String msg) {
        return new ResponseBean<>(-1, msg, null);
    }

    public static <T> ResponseBean<T> fail(String msg, T data) {
        return new ResponseBean<>(-1, msg, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
