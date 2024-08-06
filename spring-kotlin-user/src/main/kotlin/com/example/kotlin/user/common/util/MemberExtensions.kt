package com.example.kotlin.user.common.util

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.dto.MemberDtoRequest
import com.example.kotlin.user.dto.MemberDtoResponse
import com.example.kotlin.user.model.entity.MemberEntity

fun MemberDtoRequest.toDomain(): Member {
    return Member(
        id = this.id,
        loginId = this.loginId,
        password = this.password,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
    )
}


fun MemberEntity.toDomain(): Member {
    return Member(
        id = this.id,
        loginId = this.loginId,
        password = this.password,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
    )
}

fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = this.id,
        loginId = this.loginId,
        password = this.password,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
    )
}

fun Member.toDtoResponse(): MemberDtoResponse {
    return MemberDtoResponse(
        id = this.id!!,
        loginId = this.loginId,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
    )
}



