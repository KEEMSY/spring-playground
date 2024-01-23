package spring.playground.springdata.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.playground.springdata.persistence.entity.member.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
