package spring.playground.springdata.service.adapter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.repository.MemberJpaRepository;
import spring.playground.springdata.service.MemberPort;

import java.util.List;

@Component
public class MemberAdapter implements MemberPort {
    private final MemberJpaRepository memberJpaRepository;

    public MemberAdapter(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberJpaRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberJpaRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    @Override
    public List<Member> findMembers() {
        return memberJpaRepository.findAll();
    }

    @Override
    public Member findOne(Long memberId) {
        return memberJpaRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
    }

    @Override
    @Transactional
    public void update(Long id, String name) {
        Member member = memberJpaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
        member.setName(name);
    }
}
