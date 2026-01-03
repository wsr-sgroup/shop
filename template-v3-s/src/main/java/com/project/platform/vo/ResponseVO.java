package com.project.platform.vo;

import java.io.Serializable;

public class ResponseVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseVO<T> ok() {
        return new ResponseVO<>(200, "操作成功", null);
    }

    public static <T> ResponseVO<T> ok(T data) {
        return new ResponseVO<>(200, "操作成功", data);
    }

    public static <T> ResponseVO<T> success() {
        return ok();
    }

    public static <T> ResponseVO<T> success(T data) {
        return ok(data);
    }

    public static <T> ResponseVO<T> fail(int code, T data) {
        return new ResponseVO<>(code, "操作失败", data);
    }

    public static <T> ResponseVO<T> fail(int code, String msg, T data) {
        return new ResponseVO<>(code, msg, data);
    }

    public static <T> ResponseVO<T> fail(int code, String msg) {
        return new ResponseVO<>(code, msg, null);
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