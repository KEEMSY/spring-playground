package com.example.kotlin.user.common.exception

import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.status.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.servlet.resource.NoResourceFoundException

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

    @ExceptionHandler(BadCredentialsException::class)
    protected fun badCredentialsException(ex: BadCredentialsException):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("로그인 실패" to "아이디 혹은 비밀번호를 다시 확인하세요.")
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg
            ), HttpStatus.BAD_REQUEST
        )
    }
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoHandlerFoundException(ex: NoResourceFoundException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("error" to "Requested resource not found: ${ex.resourcePath}")
        return ResponseEntity(
            BaseResponse(
                ResultCode.NOT_FOUND.name,
                errors,
                "요청한 리소스를 찾을 수 없습니다."
            ), HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    protected fun handleAccessDeniedException(ex: AccessDeniedException):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("error" to "Access Denied")
        return ResponseEntity(
            BaseResponse(
                ResultCode.FORBIDDEN.name,
                errors,
                "접근 권한이 없습니다."
            ), HttpStatus.FORBIDDEN
        )
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        // 예상하지 못한 예외 처리
        val errors = mapOf(" " to (ex.message ?: "Raise UnExpected Exception"))
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.msg + ": ${ex}"
            ), HttpStatus.BAD_REQUEST
        )
    }
}