package com.example.kotlin.user.business.domain

import com.example.kotlin.user.common.status.ROLE


data class MemberRole (
    val id: Long?,
    val role: ROLE,
    val member: Member
)