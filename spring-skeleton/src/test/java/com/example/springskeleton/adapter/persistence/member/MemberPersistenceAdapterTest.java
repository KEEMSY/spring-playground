package com.example.springskeleton.adapter.persistence.member;

import com.example.springskeleton.DatabaseCleanUp;
import com.example.springskeleton.adapter.member.persistence.MemberJpaRepository;
import com.example.springskeleton.adapter.member.persistence.MemberPersistenceAdapter;
import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.domain.member.Role;
import com.example.springskeleton.steps.MemberStep;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class MemberPersistenceAdapterTest {
    private final MemberPersistenceAdapter memberPersistenceAdapter;
    private final MemberJpaRepository memberJpaRepository;

    private final MemberStep memberStep;
    private final DatabaseCleanUp databaseCleanup;

    @Autowired
    MemberPersistenceAdapterTest(MemberPersistenceAdapter memberPersistenceAdapter,
                                 MemberJpaRepository memberJpaRepository,
                                 MemberStep memberStep,
                                 DatabaseCleanUp databaseCleanup) {
        this.memberPersistenceAdapter = memberPersistenceAdapter;
        this.memberJpaRepository = memberJpaRepository;
        this.memberStep = memberStep;
        this.databaseCleanup = databaseCleanup;
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() {
        // given

        Address address = memberStep.createAddress();
        Set<Role> roles = Set.of(Role.USER);

        Member member = memberStep.createMemberEntity(address, roles);

        // when
        long createdMemberId = memberPersistenceAdapter.save(member);

        // then
        Member foundMember = memberJpaRepository.findById(createdMemberId).orElseThrow();
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(foundMember.getName()).isEqualTo(member.getName());
        assertThat(foundMember.getRoles()).isEqualTo(member.getRoles());

    }

    @Test
    @DisplayName("중복된 이메일로 저장 시 에러가 발생한다.")
    void createUserWithDuplicatedEmail() {
        // given
        Address address = memberStep.createAddress();
        Set<Role> roles = Set.of(Role.USER);

        Member member = memberStep.createMemberEntity(address, roles);
        memberPersistenceAdapter.save(member);

        // when, then
        Member member2 = memberStep.createMemberEntity(address, roles);
        assertThatThrownBy(() -> memberPersistenceAdapter.save(member2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 존재하는 이메일입니다.");
    }

}