package com.example.springuser.jwt;

import com.example.springuser.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
응답 헤더에 바로 넣는 방법 -> 헤더의 키 값도 지정해야 함
토큰에 담아서 넣는 방법
 */
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자: 규칙이라고 생각하면 편함
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분, 단위는 ms

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(String username, UserRole role) {
        Date date = new Date();

        String token = Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID) <- PK 값을 사용해도 좋음
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
        logger.info("create!! Token: " + token);
        // 24.09.05 기록: BEARER_PREFIX 는 필요 없는 것으로 판단됨(해당 로직에 추가 시 에러가 발생함, 혹은 해당 BEARER_PREFIX를 사용하고, 요청시 마다 BEARER_PREFIX을 추가하지 않는 경우에도 에러가 발생 하였음
        // 이로 인해 해당 코드를 주석처리 진행함
        return   // BEARER_PREFIX +
                token;
    }

    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // JWT 토큰을 URL 인코딩하여 공백 및 특수 문자를 처리
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            // 쿠키 생성
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);  // XSS 공격 방지
            cookie.setSecure(true);    // HTTPS에서만 전송
            cookie.setMaxAge(3600);    // 쿠키 유효 기간 설정 (예: 1시간)

            // Response 객체에 쿠키 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            // 인코딩 예외 처리
            logger.error(e.getMessage());
        }
    }


    // JWT 토큰 substring, token 값만 가져옴
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // header 및 쿠키에서 JWT 가져오기
    // 기록용: JWT를 가져오는 로직에서 원래 헤더와 쿠키를 모두 확인하는 것이 맞는지는 확인이 필요함
    // (24.09.05 "쿠키에서 토큰 추출"은, 페이지 렌더링을 하는 과정에서 쿠키에서 토큰을 가져오지 못하는 경우를 식별하여 조치한 사항임)
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        // 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기 | 권한 정보 가져옴
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

}