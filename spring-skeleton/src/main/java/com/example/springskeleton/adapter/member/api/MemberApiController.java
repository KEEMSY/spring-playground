package com.example.springskeleton.adapter.member.api;

import com.example.springskeleton.application.MemberService;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.dto.member.MemberDto;
import com.example.springskeleton.dto.member.MemberSearchCondition;
import com.example.springskeleton.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> getMembersWithCondition(@RequestParam(value = "offset", defaultValue = "0") int limit,
                                   @RequestParam(value = "limit", defaultValue = "100") int offset) {
        MemberSearchCondition condition = MemberSearchCondition.builder()
                .limit(limit)
                .offset(offset)
                .build();
        return memberService.findMembersWith(condition);
    }

    @GetMapping("api/v1/member")
    public Member getMember(@RequestParam("memberId") Long memberId) throws MemberException {
        return memberService.getMember(memberId);
    }

    @PostMapping("api/v1/member")
    public Long joinMember(MemberDto memberDto) throws MemberException {
        return memberService.join(memberDto);
    }


}
