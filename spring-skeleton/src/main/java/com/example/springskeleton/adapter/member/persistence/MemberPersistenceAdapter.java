package com.example.springskeleton.adapter.member.persistence;

import com.example.springskeleton.application.ports.MemberRepository;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.dto.member.MemberSearchCondition;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Long save(Member member) {
        try {
            return memberJpaRepository.save(member).getId();
        } catch (DataIntegrityViolationException ex) {
            Throwable rootCause = ex.getRootCause();
            if (rootCause instanceof ConstraintViolationException) {
                throw new IllegalStateException("유효하지 않은 값입니다.");
            }
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    @Override
    public Member findMember(Long memberId) {
        return null;
    }

    @Override
    public List<Member> findMembersBy(MemberSearchCondition condition) {
        return null;
    }

    @Override
    public List<Member> findAllMembers() {
        return null;
    }

    @Override
    public Member updateMember(Long id, Member member) {
        return null;
    }

    @Override
    public void deleteMember(Long memberId) {

    }
}
