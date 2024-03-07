package com.example.springuser.controller;

import com.example.springuser.entity.User;
import com.example.springuser.entity.UserRole;
import com.example.springuser.security.UserDetailsImpl;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        return "index";
    }

    @GetMapping("/test")
    public String getAuthentication(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Authentication 의 Principal 에 저장된 UserDetailsImpl 을 가져옴
        User user =  userDetails.getUser();
        System.out.println("user.getUsername() = " + user.getUsername());

        return "redirect:/";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "forbidden";
    }

    @Secured(UserRole.Authority.ADMIN) // 관리자용
    @GetMapping("/secured")
    public String getProductsByAdmin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            System.out.println("authority.getAuthority() = " + authority.getAuthority());
        }

        return "redirect:/";
    }
}
