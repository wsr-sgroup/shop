package com.project.platform.vo;

import lombok.Data;

/**
 * 全局统一响应结果
 */
@Data
public class ResultVO<T> {
    // 状态码：200成功，500失败，400参数错误
    private Integer code;
    // 提示信息
    private String msg;
    // 业务数据
    private T data;

    // 成功响应（无数据）
    public static <T> ResultVO<T> ok() {
        return new ResultVO<>(200, "操作成功", null);
    }

    // 成功响应（带数据）
    public static <T> ResultVO<T> ok(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }

    // 失败响应
    public static <T> ResultVO<T> error(String msg) {
        return new ResultVO<>(500, msg, null);
    }

    // 私有构造
    private ResultVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}