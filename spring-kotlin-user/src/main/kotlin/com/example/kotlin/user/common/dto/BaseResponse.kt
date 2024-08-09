package com.example.kotlin.user.common.dto

import com.example.kotlin.user.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val resultMessage: String = ResultCode.SUCCESS.msg,
)
