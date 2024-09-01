package com.example.springuser.dto;

public class SocialUserInfoDto {
    private String id;
    private String nickname;
    private String email;

    public SocialUserInfoDto(String id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    // toString method for logging
    @Override
    public String toString() {
        return "SocialUserInfoDto{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}