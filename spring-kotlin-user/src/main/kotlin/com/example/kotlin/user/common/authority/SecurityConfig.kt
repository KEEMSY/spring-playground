package com.example.kotlin.user.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.access.AccessDeniedException

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {
    /*
    * SecurityFilterChain
    * - httpBasic { it.disable() } : basic auth 끄기(HTTP Basic 인증을 비활성화)
    * - .csrf { it.disable() } : csrf 끄기, 주로 REST API에서 사용
    * - .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } : JWT를 사용하기 때문에 세션은 사용하지 않음
    * - .addFilterBefore(A, B) : B A (A B ): B 실행하기 전에 A 필터 실행하기(A가 통과하면, B 실행는 실행 안함)
    *
    * authorizeHttpRequests: 요청에 대한 권한을 설정
    * - hasRole('role1') (role1): 권한(role1)을 가지고 있는 경우
    * - hasAnyRole('role1', 'role2'): 권한들(role1, role2) 중 하나라도 가지고 있는 경우, 갯수 제한은 없음
    * - permitAll(): 권한 있든 말든 모두 접근 가능하다.
    * - denyAll() : 권한 있든 말든 모두 접근 불가능하다.
    * - anonymous(): Anonymous 사용자일 경우(인증을 하지 않은 사용자)
    * - rememberMe(): Remember-me기능으로 로그인한 사용자일 경우
    * - authenticated(): Anonymous 사용자가 아닌 경우(인증을 한 사용자)
    * - fullyAuthenticated(): Anonymous 사용자가 아니고  Remember-me 기능으로 로그인을 하지 않은 사용자일 경우
    *
    * [ 잎으로 작업해야 할 사항 ]
    *  Swagger 는 관리자(혹은 개발자)만 접근 가능하도록 권한을 수정 할 것
    *  실제 프로덕션 환경에서는 보안 요구사항에 따라 더 세밀한 접근 제어가 필요함
    *  - 특정 관리자 기능에 대한 추가적인 인증 요구
    *  - API 버전별 다른 인증 정책 적용
    *  - 역할 기반 접근 제어(RBAC) 구현
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/api/members/**")
                    .authenticated()
                it.anyRequest().permitAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, authException ->
                    throw AccessDeniedException("Authentication failed")
                }
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers("/js/**", "/css/**", "/images/**") // 정적 리소스 ignore 설정
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}