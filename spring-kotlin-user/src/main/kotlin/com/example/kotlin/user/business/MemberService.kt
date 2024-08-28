package com.example.kotlin.user.business

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.business.port.MemberRepository
import com.example.kotlin.user.common.authority.JwtTokenProvider
import com.example.kotlin.user.common.authority.TokenInfo
import com.example.kotlin.user.common.exception.InvalidInputException
import com.example.kotlin.user.common.exception.fail
import com.example.kotlin.user.common.util.toDomain
import com.example.kotlin.user.dto.LoginDto
import com.example.kotlin.user.dto.MemberDtoRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
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
        member = memberDtoRequest.toDomain()
        return memberRepository.save(member)
    }

    /**
     * 로그인
     */
    @Transactional
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken =
            UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        val authentication =
            authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        return jwtTokenProvider.createToken(authentication)
    }

    /**
     * 내 정보 조회
     */
    fun searchMyInfo(id: Long): Member {
        return memberRepository.findById(id)
            ?: throw InvalidInputException("회원 정보를 찾을 수 없습니다.: $id")
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    fun modifyMyInfo(memberDtoRequest: MemberDtoRequest): Member {
        val member = memberRepository.findById(memberDtoRequest.id!!)
            ?: throw InvalidInputException("회원 정보를 찾을 수 없습니다.: ${memberDtoRequest.id}")

        val updatedMember = member.copy(
            loginId = memberDtoRequest.loginId ?: member.loginId,
            password = memberDtoRequest.password ?: member.password,
            name = memberDtoRequest.name ?: member.name,
            birthDate = memberDtoRequest.birthDate ?: member.birthDate,
            gender = memberDtoRequest.gender ?: member.gender,
            email = memberDtoRequest.email ?: member.email,
        )

        return memberRepository.save(updatedMember)
    }

}