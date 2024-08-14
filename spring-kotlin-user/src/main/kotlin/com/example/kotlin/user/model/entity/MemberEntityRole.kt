package com.example.kotlin.user.model.entity

import com.example.kotlin.user.common.status.ROLE
import jakarta.persistence.*

@Entity
class MemberEntityRole(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: ROLE,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "fk_user_role_member_id"))
    val member: MemberEntity,
)