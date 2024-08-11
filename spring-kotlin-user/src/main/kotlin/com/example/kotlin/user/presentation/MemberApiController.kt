package com.example.kotlin.user.presentation

import com.example.kotlin.user.business.MemberService
import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.util.toDtoResponse
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
     * ResponseEntity 으로 대체해야 함
     */
    @PostMapping("/v1/sign-up")
    fun signUpV1(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): MemberDtoResponse {
        val member: Member =  memberService.signUp(memberDtoRequest=memberDtoRequest)
        return member.toDtoResponse()
    }

    @PostMapping("/v2/sign-in/")
    fun signUpV2(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<MemberDtoResponse> {
        val member: Member =  memberService.signUp(memberDtoRequest=memberDtoRequest)
        return BaseResponse(
            message = "회원 가입 성공",
            data = member.toDtoResponse()
        )
    }
}