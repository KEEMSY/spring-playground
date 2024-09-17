package com.example.springuser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    Long id;
    String username;
    boolean isAdmin;
}