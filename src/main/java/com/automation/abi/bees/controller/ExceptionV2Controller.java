package com.automation.abi.bees.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.automation.abi.bees.entity.ErrorResult;
import com.automation.abi.bees.util.UserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.automation.abi")
public class ExceptionV2Controller {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // Arument 잘못 날렸을 경우
    // 서블릿 컨테이너까지 지저분하게 가지 않고, 이전에 리졸버에서 캐치
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegaErrorResult(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("500", "Internal Error");
    }

}
