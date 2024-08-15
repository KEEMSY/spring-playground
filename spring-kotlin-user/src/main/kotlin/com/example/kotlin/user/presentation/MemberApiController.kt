package com.example.kotlin.user.presentation

import com.example.kotlin.user.business.MemberService
import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.common.authority.TokenInfo
import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.util.toDtoResponse
import com.example.kotlin.user.dto.LoginDto
import com.example.kotlin.user.dto.MemberDtoRequest
import com.example.kotlin.user.dto.MemberDtoResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/members")
@RestController
class MemberApiController(
    private val memberService: MemberService
) {
    /**
     * 회원 가입
     */
    @PostMapping("/v1/sign-up")
    fun signUpV1(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): MemberDtoResponse {
        val member: Member =  memberService.signUp(memberDtoRequest=memberDtoRequest)
        return member.toDtoResponse()
    }

    @PostMapping("/v2/sign-up/")
    fun signUpV2(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<MemberDtoResponse> {
        val member: Member =  memberService.signUp(memberDtoRequest=memberDtoRequest)
        return BaseResponse(
            message = "회원 가입 성공",
            data = member.toDtoResponse()
        )
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }
}