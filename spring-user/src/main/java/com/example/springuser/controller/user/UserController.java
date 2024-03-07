package com.example.springuser.controller.user;

import com.example.springuser.dto.LoginRequest;
import com.example.springuser.dto.SignupRequest;
import com.example.springuser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}