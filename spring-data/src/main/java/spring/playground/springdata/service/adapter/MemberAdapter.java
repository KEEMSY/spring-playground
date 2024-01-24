package spring.playground.springdata.service.adapter;

import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.service.MemberPort;

import java.util.List;

public class MemberAdapter implements MemberPort {
    @Override
    public Long join(Member member) {
        return null;
    }

    @Override
    public List<Member> findMembers() {
        return null;
    }

    @Override
    public Member findOne(Long memberId) {
        return null;
    }

    @Override
    public void Update(Long id, String name) {

    }
}
