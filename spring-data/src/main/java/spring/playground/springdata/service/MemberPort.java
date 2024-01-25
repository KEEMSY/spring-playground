package spring.playground.springdata.service;

import spring.playground.springdata.persistence.entity.member.Member;

import java.util.List;

public interface MemberPort {
    Long join(Member member);
    List<Member> findMembers();
    Member findOne(Long memberId);
    void update(Long id, String name);

}
