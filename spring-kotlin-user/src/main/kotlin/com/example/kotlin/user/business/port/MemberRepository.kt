package com.example.kotlin.user.business.port

import com.example.kotlin.user.business.domain.Member
import org.springframework.stereotype.Component

@Component
interface MemberRepository {
    fun findByLoginId(loginId: String): Member?
    fun save(member: Member): Member
}