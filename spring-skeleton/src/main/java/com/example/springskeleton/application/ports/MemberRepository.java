package com.example.springskeleton.application.ports;

import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.dto.member.MemberSearchCondition;

import java.util.List;

public interface MemberRepository {

    Long save(Member member);
    Member findMember(Long memberId);
    List<Member> findMembersBy(MemberSearchCondition condition);
    List<Member> findAllMembers();
    Member updateMember(Long id, Member member);
    void deleteMember(Long memberId);
}
