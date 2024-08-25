package com.example.kotlin.user.common.service

import com.example.kotlin.user.common.dto.CustomUser
import com.example.kotlin.user.model.entity.MemberEntity
import com.example.kotlin.user.model.repository.MemberJpaRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberJpaRepository: MemberJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        memberJpaRepository.findByLoginId(username)
            ?.let { createUserDetails(it) }
            ?: throw UsernameNotFoundException(" .")

    private fun createUserDetails(memberEntity: MemberEntity): UserDetails =
        CustomUser(
            memberEntity.id!!,
            memberEntity.loginId,
            passwordEncoder.encode(memberEntity.password),
            memberEntity.roles!!.map { SimpleGrantedAuthority("ROLE_${it.role}") }
        )

}