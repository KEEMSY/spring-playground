package com.example.kotlin.user.common.status

enum class ResultCode(val msg: String) {
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생했습니다."),
    FORBIDDEN("권한이 없습니다."),
    NOT_FOUND("리소스를 찾을 수 없습니다."),
}