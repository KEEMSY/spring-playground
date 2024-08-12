package com.example.kotlin.user.common.authority

data class TokenInfo(
    /**
     * grantType: JWT 권한 인증 타입(ex. Bearer)
     * accessToken: 실제 검증할 때 확인할 토큰
     * [ ]추가할 수 있는 내용 ]
     * - refreshToken: 토큰 갱신을 위한 토큰
     */
    val grantType: String,
    val accessToken: String,
)
