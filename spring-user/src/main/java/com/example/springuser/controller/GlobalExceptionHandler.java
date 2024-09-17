package com.example.springuser.controller;

import com.example.springuser.dto.BaseResponse;
import com.example.springuser.dto.ResultCode;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleEntityNotFoundException(EntityNotFoundException e) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setResultCode(ResultCode.ERROR.name());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<BaseResponse<String>> handleLazyInitializationException(LazyInitializationException e) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setResultCode(ResultCode.ERROR.name());
        response.setMessage("데이터를 불러오는 중 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 다른 예외들에 대한 처리가 필요할 경우 추가
}
