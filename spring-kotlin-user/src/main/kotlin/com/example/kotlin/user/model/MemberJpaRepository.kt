package com.example.kotlin.user.model

import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository: JpaRepository<MemberEntity, Long>{
    fun findByLoginId(loginId: String): MemberEntity?
    fun findByEmail(email: String): MemberEntity?
    fun findByLoginIdOrEmail(loginId: String, email: String): MemberEntity?
}