package com.example.springuser.controller.user;

import com.example.springuser.dto.LoginRequest;
import com.example.springuser.dto.SignupRequest;
import com.example.springuser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public String signup(SignupRequest request) {
        userService.signup(request);
        return "redirect:/api/user/login-page";
    }

    @PostMapping("/user/login")
    public String login(LoginRequest request, HttpServletResponse response) {
        try {
            userService.login(request, response);
        } catch (Exception e) {
            log.info("로그인 실패: {}", e.getMessage());
            return "redirect:/api/user/login-page?error";
        }

        return "redirect:/";
    }
}