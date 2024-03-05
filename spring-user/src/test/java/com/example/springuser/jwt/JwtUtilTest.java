package com.example.springuser.jwt;

import com.example.springuser.entity.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("토큰 생성 테스트 - 생성된 토큰은 null이 아니며 Bearer 접두사로 시작해야 한다.")
    void createTokenTest() {
        // given
        String username = "testUser";
        UserRole role = UserRole.USER;

        // when
        String token = jwtUtil.createToken(username, role);

        // then
        assertNotNull(token, "생성된 토큰은 null이 아니어야 한다.");
        assertTrue(token.startsWith(JwtUtil.BEARER_PREFIX), "토큰은 Bearer 접두사로 시작해야 한다.");
    }

    @Test
    @DisplayName("JWT 쿠키 추가 테스트 - 주어진 토큰을 쿠키에 올바르게 추가해야 한다.")
    void addJwtToCookieTest() throws UnsupportedEncodingException {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = JwtUtil.BEARER_PREFIX + "dummyToken";
        String encodedToken = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

        // when
        jwtUtil.addJwtToCookie(token, response);

        // then
        verify(response).addCookie(argThat(cookie ->
                cookie.getName().equals(JwtUtil.AUTHORIZATION_HEADER) &&
                        cookie.getValue().equals(encodedToken)
        ));
    }

    @Test
    @DisplayName("토큰 유효성 검증 테스트 - 유효한 토큰은 성공적으로 검증되어야한다.")
    void validateTokenTest() {
        // given
        String username = "testUser";
        UserRole role = UserRole.USER;
        String createdToken = jwtUtil.createToken(username, role);
        String token = jwtUtil.substringToken(createdToken);
        System.out.println("token = " + createdToken);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertTrue(isValid, "유효한 토큰은 성공적으로 검증되어야 한다.");
    }

    // 추가 테스트 케이스 작성...
}