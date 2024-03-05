package com.example.springskeleton.dto.member;

import com.example.springskeleton.domain.common.Address;

import java.util.Set;

public record MemberUpdateDto(
        String name,
        String email,
        String password,
        Address address,
        Set<String> roles
){

}

