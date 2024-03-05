package com.example.springskeleton.dto.member;

import lombok.Builder;

@Builder
public record MemberSearchCondition(String username, String email, Integer limit, Integer offset) {
}
