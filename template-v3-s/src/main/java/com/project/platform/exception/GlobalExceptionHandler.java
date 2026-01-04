package com.project.platform.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.platform.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;


/**
 * 全局异常拦截
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseEntity CustomExceptionHandler(CustomException e) {
        return new ResponseEntity<>(ResponseVO.fail(e.getHttpStatus().value(), e.getMessage()), e.getHttpStatus());
    }
    
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity ExceptionHandler(Exception e) {
        log.error("系统异常：", e);
        return new ResponseEntity<>(ResponseVO.fail(500, e.getMessage()), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
