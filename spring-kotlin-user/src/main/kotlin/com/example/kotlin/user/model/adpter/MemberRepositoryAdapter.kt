package com.example.kotlin.user.model.adpter

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.business.port.MemberRepository
import com.example.kotlin.user.common.status.ROLE
import com.example.kotlin.user.common.util.toDomain
import com.example.kotlin.user.common.util.toEntity
import com.example.kotlin.user.model.entity.MemberEntity
import com.example.kotlin.user.model.entity.MemberEntityRole
import com.example.kotlin.user.model.repository.MemberJpaRepository
import com.example.kotlin.user.model.repository.MemberRoleJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository,
    private val memberRoleJpaRepository: MemberRoleJpaRepository
): MemberRepository {
    override fun findByLoginId(loginId: String): Member? {
        val memberEntity: MemberEntity? =  memberJpaRepository.findByLoginId(loginId)
        return memberEntity?.toDomain()

    }

    @Transactional()
    override fun save(member: Member): Member {
        // 사용자 정보 저장
        val memberEntity = member.toEntity()
        val savedMemberEntity = memberJpaRepository.save(memberEntity)

        // 권한 저장
        val memberEntityRole = MemberEntityRole(
            id= null,
            member = savedMemberEntity,
            role = ROLE.MEMBER
        )
        memberRoleJpaRepository.save(memberEntityRole)

        // 현재 문제점: ROLE이 반환객체 내에 빈 값으로 나오는 오류
        // 해결 방법 1. 새롭게 다시 조회하여 해당 값을 Domain 객체로 전환한다.
        // 해결 방법 2. toDomain() 함수를 수정하여 roles를 함께 전달한다.
        // 해결 방법 3. 반환하는 데이터를 수정한다.(id 값만 반환하는 등)
        return savedMemberEntity.toDomain()
    }
}