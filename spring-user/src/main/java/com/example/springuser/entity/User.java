package com.example.springuser.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 TODO
 - 기본적인 로그인 데이터 뿐만아니라, 고객 세분화를 하기 위한 세부 정보를 기입할 수 있도록 확장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "\"users\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_provider")
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    public User(String username, String password, String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User(String username, String password, String email, UserRole role, String socialId, SocialProvider socialProvider) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.socialId = socialId;
        this.socialProvider = socialProvider;
    }

    public User updateSocialInfo(String socialId, SocialProvider socialProvider) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
        return this;
    }
}