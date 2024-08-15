package com.example.kotlin.user.business.domain

import com.example.kotlin.user.common.status.Gender
import java.time.LocalDate

data class Member (
    val id: Long?,
    val loginId: String,
    val password: String,
    val name: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val email: String,
    val roles: List<MemberRole>? = null
)