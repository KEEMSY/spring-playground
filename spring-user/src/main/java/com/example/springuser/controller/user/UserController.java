package com.example.springuser.controller.user;

import com.example.springuser.dto.BaseResponse;
import com.example.springuser.dto.SignupRequest;
import com.example.springuser.dto.UserInfoDto;
import com.example.springuser.entity.SocialProvider;
import com.example.springuser.entity.UserRole;
import com.example.springuser.jwt.JwtUtil;
import com.example.springuser.security.UserDetailsImpl;
import com.example.springuser.service.SocialLoginService;
import com.example.springuser.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final SocialLoginService socialLoginService;


    public UserController(UserService userService, SocialLoginService socialLoginService) {
        this.userService = userService;
        this.socialLoginService = socialLoginService;
    }

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
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

    @GetMapping("/{provider}/callback")
    public String socialLoginCallback(@PathVariable String provider,
                                      @RequestParam String code,
                                      HttpServletResponse response) throws JsonProcessingException {
        log.info("Social login callback - provider: {}, code: {}", provider, code);

        SocialProvider socialProvider = SocialProvider.valueOf(provider.toUpperCase());
        String token = socialLoginService.socialLogin(code, socialProvider);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }


    /*
     팔로잉 기능
    - 개선해야 하는 부분
      - 팔로잉은 자기 자신을 할 수 없다.
      - 팔로잉/언팔로우 는 요청자와 대상 유저의 정보가 일치해야만 가능하다.
     */
    @PostMapping("/{followerId}/follow/{followedId}")
    public ResponseEntity<BaseResponse<Void>> followUser(@PathVariable Long followerId, @PathVariable Long followedId) {
        userService.followUser(followerId, followedId);
        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage("Successfully followed user");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{followerId}/unfollow/{followedId}")
    public ResponseEntity<BaseResponse<Void>> unfollowUser(@PathVariable Long followerId, @PathVariable Long followedId) {
        userService.unfollowUser(followerId, followedId);
        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage("Successfully unfollowed user");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse<List<UserInfoDto>>> getFollowers(@PathVariable Long userId) {
        List<UserInfoDto> followers = userService.getFollowers(userId);
        System.out.println();
        System.out.println("followers = " + followers);
        System.out.println();
        return ResponseEntity.ok(new BaseResponse<>(followers));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<BaseResponse<List<UserInfoDto>>> getFollowing(@PathVariable Long userId) {
        List<UserInfoDto> following = userService.getFollowing(userId);
        return ResponseEntity.ok(new BaseResponse<>(following));
    }
}