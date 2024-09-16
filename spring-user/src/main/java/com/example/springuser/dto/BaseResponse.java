package com.example.springuser.dto;


public class BaseResponse<T> {
    private String resultCode;
    private T data;
    private String message;

    // 기본 생성자: resultCode와 message의 기본값 설정
    public BaseResponse() {
        this.resultCode = ResultCode.SUCCESS.name();
        this.message = ResultCode.SUCCESS.getMsg();
    }

    // data 필드를 사용하는 생성자
    public BaseResponse(T data) {
        this();
        this.data = data;
    }

    // 전체 필드를 받는 생성자
    public BaseResponse(String resultCode, T data, String message) {
        this.resultCode = resultCode;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
