package com.example.kotlin.user.common.exception

import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.status.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleValidationExceptions(ex: MethodArgumentNotValidException):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        // @Valid 유효성 검사 실패 시 발생하는 예외 처리
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        // 입력 값이 올바르지 않은 경우 발생하는 예외 처리
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        // 그 외 예외 처리
        val errors = mapOf(" " to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
            ), HttpStatus.BAD_REQUEST
        )
    }
}