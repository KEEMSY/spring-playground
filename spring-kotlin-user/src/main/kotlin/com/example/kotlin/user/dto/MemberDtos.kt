package com.example.kotlin.user.dto

import com.example.kotlin.user.common.status.Gender
import java.time.LocalDate

data class MemberDtoRequest(
    val id: Long?,
    val loginId: String,
    val password: String,
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val email: String,
)

data class MemberDtoResponse(
    val id: Long,
    val loginId: String,
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val email: String,
)
