package com.example.springskeleton.application;

import com.example.springskeleton.application.ports.MemberRepository;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.dto.member.MemberDto;
import com.example.springskeleton.dto.member.MemberSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long join(MemberDto memberDto) {
        Member member = new Member();
        member.setName(memberDto.name());
        member.setEmail(memberDto.email());
        member.setPassword(bCryptPasswordEncoder.encode(memberDto.password()));

        return memberRepository.save(member);
    }

    public Member findMember(Long memberId) {
        return memberRepository.findMember(memberId);
    }

    public void updateMember(Member member) {
        memberRepository.updateMember(member.getId(), member);
    }

    public void deleteMember(Long memberId) {
        memberRepository.deleteMember(memberId);
    }

    public List<Member> findMembersWith(MemberSearchCondition condition) {
        return memberRepository.findMembersBy(condition);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAllMembers();
    }
}
