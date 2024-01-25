package spring.playground.springdata.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.playground.springdata.persistence.entity.member.Member;

import java.util.List;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    List<Member> findByName(String name);
}
