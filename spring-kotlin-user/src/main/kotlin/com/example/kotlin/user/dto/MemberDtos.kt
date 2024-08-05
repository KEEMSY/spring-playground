package com.example.kotlin.user.dto

import com.example.kotlin.user.common.annotation.ValidEnum
import com.example.kotlin.user.common.exception.fail
import com.example.kotlin.user.common.status.Gender
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MemberDtoRequest(
    val id: Long?,

    @field:NotBlank
    @JsonProperty("loginId")
    private val _loginId: String?,

    @field:NotBlank
    @field:Pattern(
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문 , 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
    )
    @JsonProperty("password")
    private val _password: String?,

    @field:NotBlank
    @JsonProperty("name")
    private val _name: String?,

    @field:NotBlank
    @JsonProperty("birthDate")
    private val _birthDate: String?,

    @field:NotBlank
    @field:ValidEnum(enumClass = Gender::class, message = "MAN 혹은 WOMAN 중 하나의 값을 입력해주세요.")
    @JsonProperty("gender")
    private val _gender: String?,

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    private val _email: String?,
) {
    val loginId: String
        get() = _loginId ?: fail("loginId is null")
    val password: String
        get() = _password ?: fail("password is null")

    val name: String
        get() = _name ?: fail("name is null")
    val birthDate: LocalDate
        get() = _birthDate?.toLocalDate() ?: fail("birthDate is null")
    val gender: Gender
        get() = Gender.valueOf(_gender ?: fail("Gender is null"))
    val email: String
        get() = _email ?: throw IllegalArgumentException("email is null")

    private fun String.toLocalDate(): LocalDate {
        return LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}

data class MemberDtoResponse(
    val id: Long,
    val loginId: String,
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val email: String,
)
