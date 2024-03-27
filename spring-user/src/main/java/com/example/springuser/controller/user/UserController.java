package com.example.springuser.controller.user;

import com.example.springuser.dto.SignupRequest;
import com.example.springuser.dto.UserInfoDto;
import com.example.springuser.entity.UserRole;
import com.example.springuser.jwt.JwtUtil;
import com.example.springuser.security.UserDetailsImpl;
import com.example.springuser.service.KakaoService;
import com.example.springuser.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    public UserController(UserService userService, KakaoService kakaoService) {
        this.userService = userService;
        this.kakaoService = kakaoService;
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequest request, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> errors = bindingResult.getFieldErrors();
        if (!errors.isEmpty()) {
            for (FieldError error : errors) {
                log.error(error.getField() + " field = 가" + error.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(request);
        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRole role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRole.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }

    @GetMapping("/user/kakao/callback")
    public String kakaoCallback(@RequestParam String code, HttpServletResponse res) throws JsonProcessingException {
        log.info("code = {}", code);
        String token = kakaoService.kakaoLogin(code);
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7)); // Bearer 띄어쓰기를 맞추기 위함
        cookie.setPath("/");
        res.addCookie(cookie);

        return "redirect:/";
    }
}