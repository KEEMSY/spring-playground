package com.example.kotlin.user.presentation

import com.example.kotlin.user.business.MemberService
import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.common.authority.TokenInfo
import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.dto.CustomUser
import com.example.kotlin.user.common.util.toDtoResponse
import com.example.kotlin.user.dto.LoginDto
import com.example.kotlin.user.dto.MemberDtoRequest
import com.example.kotlin.user.dto.MemberDtoResponse
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.AccessDeniedException

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
        val member: Member = memberService.signUp(memberDtoRequest = memberDtoRequest)
        return member.toDtoResponse()
    }

    @PostMapping("/v2/sign-up/")
    fun signUpV2(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<MemberDtoResponse> {
        val member: Member = memberService.signUp(memberDtoRequest = memberDtoRequest)
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

    /**
     * 내 정보 조회하기
     * - 문제점: 다른 유저의 정보까지 조회가 가능 함
     */
    @GetMapping("/info1/{id}")
    fun searchMyInfo(@PathVariable id: Long): BaseResponse<MemberDtoResponse> {
        val member = memberService.searchMyInfo(id)
        return BaseResponse(data = member.toDtoResponse())
    }

    /**
     * 내 정보 조회하기 v2
     * - 문제점: 다른 유저의 정보까지 조회가 가능 함
     */
    @GetMapping("/info2/{id}")
    fun searchMyInfoV2(@PathVariable id: Long): BaseResponse<MemberDtoResponse> {
        val userId = (SecurityContextHolder
            .getContext()
            .authentication
            .principal as CustomUser)
            .userId

        if (userId != id) {
            throw AccessDeniedException("해당 정보에 접근할 수 없습니다.")
        }
        val member = memberService.searchMyInfo(id)
        return BaseResponse(data = member.toDtoResponse())
    }

    /**
     * 내 정보 조회하기
     * - 로그인한 사람의 정보만 조회할 수 있도록 수정
     */
    @GetMapping("/info3")
    fun searchMyInfo(): BaseResponse<MemberDtoResponse> {
        val userId = (SecurityContextHolder
            .getContext()
            .authentication
            .principal as CustomUser)
            .userId
        val member = memberService.searchMyInfo(userId)

        return BaseResponse(data = member.toDtoResponse())
    }

    /**
     * 회원 정보 수정하기
     */
    @PutMapping("/info")
    fun modifyMyInfo(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<MemberDtoResponse> {
        val userId = (SecurityContextHolder
            .getContext()
            .authentication
            .principal as CustomUser)
            .userId
        memberDtoRequest.id = userId
        val member = memberService.modifyMyInfo(memberDtoRequest)
        return BaseResponse(data = member.toDtoResponse())
    }
}

