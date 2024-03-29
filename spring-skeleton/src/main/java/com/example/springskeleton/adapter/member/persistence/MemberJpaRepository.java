package com.example.springskeleton.adapter.member.persistence;

import com.example.springskeleton.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
