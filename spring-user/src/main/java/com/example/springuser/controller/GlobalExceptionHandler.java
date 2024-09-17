package com.example.springuser.controller;

import com.example.springuser.dto.BaseResponse;
import com.example.springuser.dto.ResultCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setResultCode(ResultCode.ERROR.name());
        response.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 다른 예외들에 대한 처리가 필요할 경우 추가
}
