package com.example.kotlin.user.model.adpter

import com.example.kotlin.user.business.domain.Member
import com.example.kotlin.user.business.port.MemberRepository
import com.example.kotlin.user.common.util.toDomain
import com.example.kotlin.user.common.util.toEntity
import com.example.kotlin.user.model.entity.MemberEntity
import com.example.kotlin.user.model.repository.MemberJpaRepository
import org.springframework.stereotype.Component

@Component
class MemberRepositoryAdapter(
    private val memberJpaRepository: MemberJpaRepository
): MemberRepository {
    override fun findByLoginId(loginId: String): Member? {
        val memberEntity: MemberEntity? =  memberJpaRepository.findByLoginId(loginId)
        return memberEntity?.toDomain()

    }

    override fun save(member: Member): Member {
        val memberEntity = member.toEntity()
        val savedMemberEntity = memberJpaRepository.save(memberEntity)
        return savedMemberEntity.toDomain()
    }
}