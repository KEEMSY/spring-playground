package com.example.kotlin.user.business

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.business.port.MemberRepository
import com.example.kotlin.user.common.exception.fail
import com.example.kotlin.user.dto.MemberDtoRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService (
    private val memberRepository: MemberRepository
){
    /**
     * 회원 가입
     */
    @Transactional
    fun signUp(memberDtoRequest: MemberDtoRequest): Member {
        // 1. 회원 가입 아이디 중복 체크
        var member = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null) {
            fail("이미 가입된 아이디 입니다.")
        }

        // 2. 회원 가입
        member = Member(
            id = null,
            loginId = memberDtoRequest.loginId,
            password = memberDtoRequest.password,
            name = memberDtoRequest.name,
            birthDate = memberDtoRequest.birthDate,
            gender = memberDtoRequest.gender,
            email = memberDtoRequest.email,
        )

        return memberRepository.save(member)
    }
}