package com.example.kotlin.model.repository

import com.example.kotlin.model.entity.ExampleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExampleJpaRepository: JpaRepository<ExampleEntity, Long> {
    fun findByTitleContaining(title: String): List<ExampleEntity>
}