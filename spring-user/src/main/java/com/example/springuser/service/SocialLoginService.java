package com.example.springuser.service;

import com.example.springuser.dto.SocialUserInfoDto;
import com.example.springuser.entity.SocialProvider;
import com.example.springuser.entity.User;
import com.example.springuser.entity.UserRole;
import com.example.springuser.jwt.JwtUtil;
import com.example.springuser.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Social Login")
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    public String socialLogin(String code, SocialProvider provider) throws JsonProcessingException {
        String accessToken = getToken(code, provider);
        SocialUserInfoDto userInfo = getSocialUserInfo(accessToken, provider);
        User user = registerSocialUserIfNeeded(userInfo, provider);
        return jwtUtil.createToken(user.getUsername(), user.getRole());
    }

    private String getToken(String code, SocialProvider provider) throws JsonProcessingException {
        URI uri;
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        switch (provider) {
            case KAKAO:
                uri = UriComponentsBuilder
                        .fromUriString("https://kauth.kakao.com")
                        .path("/oauth/token")
                        .encode()
                        .build()
                        .toUri();
                body.add("grant_type", "authorization_code");
                body.add("client_id", "24d264059e6e314565e2409ca90da734"); // 해당 부분 수정 필요(카카오 API 키)
                body.add("redirect_uri", "http://localhost:9090/api/user/kakao/callback");
                body.add("code", code);
                break;
            // TODO: Add cases for NAVER, GOOGLE, APPLE
            default:
                throw new IllegalArgumentException("Unsupported social provider");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private SocialUserInfoDto getSocialUserInfo(String accessToken, SocialProvider provider) throws JsonProcessingException {
        URI uri;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        switch (provider) {
            case KAKAO:
                uri = UriComponentsBuilder
                        .fromUriString("https://kapi.kakao.com")
                        .path("/v2/user/me")
                        .encode()
                        .build()
                        .toUri();
                break;
            // TODO: Add cases for NAVER, GOOGLE, APPLE
            default:
                throw new IllegalArgumentException("Unsupported social provider");
        }

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        log.info("Social user info: " + id + ", " + nickname + ", " + email);
        return new SocialUserInfoDto(id, nickname, email);
    }

    private User registerSocialUserIfNeeded(SocialUserInfoDto userInfo, SocialProvider provider) {
        String socialId = userInfo.getId();
        User socialUser = userRepository.findBySocialIdAndSocialProvider(socialId, provider).orElse(null);

        if (socialUser != null) {
            return socialUser;
        }

        String email = userInfo.getEmail();
        User sameEmailUser = userRepository.findByEmail(email).orElse(null);
        if (sameEmailUser != null) {
            socialUser = sameEmailUser;
            socialUser = socialUser.updateSocialInfo(socialId, provider);
        } else {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            socialUser = new User(userInfo.getNickname(), encodedPassword, email, UserRole.USER, socialId, provider);
        }

        userRepository.save(socialUser);

        return socialUser;
    }
}