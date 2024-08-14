package com.example.kotlin.user.model.repository

import com.example.kotlin.user.model.entity.MemberEntityRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRoleJpaRepository : JpaRepository<MemberEntityRole, Long> {
}