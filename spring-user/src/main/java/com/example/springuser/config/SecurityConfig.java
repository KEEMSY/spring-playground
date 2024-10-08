package com.example.springuser.config;

import com.example.springuser.security.filter.JwtAuthenticationFilter;
import com.example.springuser.security.filter.JwtAuthorizationFilter;
import com.example.springuser.jwt.JwtUtil;
import com.example.springuser.security.UserDetailsServiceImpl;
// import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableMethodSecurity(securedEnabled = true) // secured 어노테이션 활성화
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil,
                          AuthenticationConfiguration authenticationConfiguration,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
//                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정, 정적 리소스의 경우, 필터를 거치는 것이 더 손해이기 때문에, 필터를 거치지 않도록 설정
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 로그인 사용
        http.formLogin((formLogin) ->
                formLogin
                        // 로그인 View 제공 (GET /api/user/login-page)
                        .loginPage("/api/user/login-page")
                        // 로그인 처리 (POST /api/user/login)
                        .loginProcessingUrl("/api/user/login")
                        // 로그인 처리 후 성공 시 URL
                        .defaultSuccessUrl("/")
                        // 로그인 처리 후 실패 시 URL
                        .failureUrl("/api/user/login-page?error")
                        .permitAll()
        );

        // filter 추가
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // 인가
        //  UsernamePasswordAuthenticationFilter 수행 전에, jwtAuthorizationFilter 수행 (인가 -> 로그인 진행이 올바른 순서이기 때문)
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 인증

        // 접근 불가 페이지
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        // "접근 불가" 페이지 URL 설정
                        .accessDeniedPage("/access-denied")
        );

        return http.build();
    }

    // 정적 리소스 Ignore
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/js/**")
                .requestMatchers("/css/**")
                .requestMatchers("/images/**");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
