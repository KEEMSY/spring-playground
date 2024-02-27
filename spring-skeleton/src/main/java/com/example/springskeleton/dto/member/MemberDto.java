package com.example.springskeleton.dto.member;

import com.example.springskeleton.domain.common.Address;


public record MemberDto(String name, String email, String password, Address address) {
}

