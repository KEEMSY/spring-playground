package com.example.springuser.dto;

public enum ResultCode {
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생했습니다."),
    FORBIDDEN("권한이 없습니다."),
    NOT_FOUND("리소스를 찾을 수 없습니다.");

    private final String msg;

    // 생성자
    ResultCode(String msg) {
        this.msg = msg;
    }

    // msg 값을 반환하는 getter 메서드
    public String getMsg() {
        return msg;
    }
}
