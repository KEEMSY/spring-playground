package spring.playground.springdata.service.adapter;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.playground.springdata.persistence.entity.common.Address;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.repository.MemberJpaRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest
class MemberAdapterTest {

    private final MemberAdapter memberAdapter;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberAdapterTest(MemberAdapter memberAdapter, MemberJpaRepository memberJpaRepository) {
        this.memberAdapter = memberAdapter;
        this.memberJpaRepository = memberJpaRepository;
    }

    @BeforeEach
    void setUp() {
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입테스트")
    void join() {
        // given
        Member member = getMember("test");

        // when
        memberAdapter.join(member);

        // then
        member = memberJpaRepository.findAll().get(0);

        assertThat(member.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("중복회원가입테스트")
    void duplicateJoinTest() {
        // given
        Member member = getMember("test");
        memberJpaRepository.save(member);

        // when, then
        assertThatThrownBy(() -> memberAdapter.join(member)).isInstanceOf(IllegalStateException.class).hasMessage("이미 존재하는 회원입니다.");
    }

    @Test
    @DisplayName("회원조회테스트")
    void findMembers() {
        // given
        Member member1 = getMember("test1");
        Member member2 = getMember("test2");
        memberJpaRepository.saveAll(List.of(member1, member2));

        // when
        List<Member> members = memberAdapter.findMembers();

        // then
        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("단일 회원 조회")
    void findOne() {
        // given
        Member member = getMember("test");
        Member otherMember = getMember("other");
        memberJpaRepository.save(member);

        // when
        Member findMember = memberAdapter.findOne(member.getId());

        // then
        assertThat(findMember.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원 수정")
    void update() {
        // given
        Member member = getMember("test");
        memberJpaRepository.save(member);

        // when
        memberAdapter.update(member.getId(), "update");

        // then
        Member findMember = memberJpaRepository.findById(member.getId()).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
        assertThat(findMember.getName()).isEqualTo("update");
    }

    @Test
    @DisplayName("회원 수정 오류: 존재하지 않을 경우 IllegalArgumentException 에러 발생")
    void updateError() {
        // given
        Member member = getMember("test");
        memberJpaRepository.save(member);

        // when, then
        assertThatThrownBy(() -> memberAdapter.update(100L, "update")).isInstanceOf(IllegalArgumentException.class).hasMessage("해당하는 회원이 없습니다.");
    }

    @NotNull
    private static Member getMember(String memberName) {
        Member member = new Member();
        member.setName(memberName);
        Address address = new Address("city", "street", "zipcode");
        member.setAddress(address);
        return member;
    }
}