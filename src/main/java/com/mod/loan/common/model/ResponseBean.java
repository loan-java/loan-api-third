package com.mod.loan.common.model;


import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.exception.BizException;

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

    public static <T> ResponseBean<T> success() {
        return new ResponseBean<>(200, "成功", null);
    }

    public static <T> ResponseBean<T> success(T data) {
        return new ResponseBean<>(200, "成功", data);
    }


    public static <T> ResponseBean<T> fail(String msg) {
        return new ResponseBean<>(ResponseEnum.M4000.getCodeInt(), msg, null);
    }

    public static <T> ResponseBean<T> fail(int code, String msg) {
        return new ResponseBean<>(code, msg, null);
    }

    public static <T> ResponseBean<T> fail(BizException e) {
        return new ResponseBean<>(e.getCodeInt(), e.getMessage(), null);
    }

    public static <T> ResponseBean<T> fail(ResponseEnum r) {
        return new ResponseBean<>(r.getCodeInt(), r.getMsg(), null);
    }

    public static <T> ResponseBean<T> fail(int code, String msg, T data) {
        return new ResponseBean<>(code, msg, data);
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
