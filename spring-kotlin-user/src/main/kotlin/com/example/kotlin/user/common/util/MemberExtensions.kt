package com.example.kotlin.user.common.util

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.business.domain.MemberRole
import com.example.kotlin.user.dto.MemberDtoRequest
import com.example.kotlin.user.dto.MemberDtoResponse
import com.example.kotlin.user.model.entity.MemberEntity
import com.example.kotlin.user.model.entity.MemberEntityRole

/*
24.08.15 기준
- Mapper 함수의 개선이 필요함
  - 순환 참조 문제를 해결하고, 도메인 객체 내 올바른 상태 값을 갖고 있도록 하는 코드 개선 필요
 */

// MemberDtoRequest를 도메인 객체로 변환
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

// 도메인 객체를 MemberDtoResponse로 변환
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


// MemberEntity를 도메인 객체로 변환
fun MemberEntity.toDomain(): Member {
    return Member(
        id = this.id,
        loginId = this.loginId,
        password = this.password,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
        roles = this.roles?.map { it.toDomain() }
    )
}

// 도메인 객체를 MemberEntity로 변환
fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = this.id,
        loginId = this.loginId,
        password = this.password,
        name = this.name,
        birthDate = this.birthDate,
        gender = this.gender,
        email = this.email,
        roles = null // 초기 변환 시 roles를 null로 설정하여 순환 참조 방지
    )
}

// MemberEntityRole을 도메인 객체로 변환
fun MemberEntityRole.toDomain(): MemberRole {
    return MemberRole(
        id = this.id,
        role = this.role,
        member = Member(
            id = this.member.id,
            loginId = this.member.loginId,
            password = this.member.password,
            name = this.member.name,
            birthDate = this.member.birthDate,
            gender = this.member.gender,
            email = this.member.email,
            roles = null // 순환 참조 방지를 위해 roles를 null로 설정
        )
    )
}

// MemberRole을 MemberEntityRole로 변환
fun MemberRole.toEntity(memberEntity: MemberEntity): MemberEntityRole {
    return MemberEntityRole(
        id = this.id,
        role = this.role,
        member = memberEntity // 이미 변환된 memberEntity 사용
    )
}


