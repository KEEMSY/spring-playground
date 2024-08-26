package com.example.kotlin.user.common.authority


import com.example.kotlin.user.common.dto.BaseResponse
import com.example.kotlin.user.common.status.ResultCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

/**
 * OncePerRequestFilter를 사용하는 주요 이유
 * 1. 요청당 한 번만 실행 보장
 * OncePerRequestFilter는 하나의 요청에 대해 필터가 한 번만 실행되도록 보장합니다. 이는 특히 포워드나 에러 페이지로의 내부 요청 처리 시 중복 실행을 방지한다.
 *
 * 2. 비동기 요청 지원:
 * 비동기 요청 처리 시에도 필터가 올바르게 동작하도록 설계되어 있다.
 *
 * 3. 명확한 필터링 로직:
 * doFilterInternal 메소드를 구현하여 실제 필터링 로직을 작성하게 되므로, 코드 구조가 더 명확해진다.
 *
 * 4. 필터 적용 여부 제어
 * shouldNotFilter 메소드를 오버라이드하여 특정 요청에 대해 필터를 적용하지 않도록 쉽게 제어할 수 있다.
 *
 * 5. 예외 처리 개선
 * 필터 체인 실행 전후로 예외 처리를 더 세밀하게 할 수 있다.
 * 이러한 특징들로 인해 OncePerRequestFilter는 JWT 인증과 같은 작업에 더 적합하며, 코드의 안정성과 가독성을 향상시킨다.
 */
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = resolveToken(request)
            if (token != null) {
                if (!jwtTokenProvider.validateToken(token)) {
                    logger.warn("Invalid JWT token")
                    handleAuthenticationException(response, "Invalid JWT token")
                    return
                }

                val authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
                logger.info("Authentication successful for token: $token")
            }
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
            handleAuthenticationException(response, "Authentication failed")
            return
        }
    }


    private fun handleAuthenticationException(response: HttpServletResponse, errorMessage: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse = BaseResponse(
            ResultCode.ERROR.name,
            mapOf("error" to errorMessage),
            ResultCode.ERROR.msg
        )
        response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
    }


    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
