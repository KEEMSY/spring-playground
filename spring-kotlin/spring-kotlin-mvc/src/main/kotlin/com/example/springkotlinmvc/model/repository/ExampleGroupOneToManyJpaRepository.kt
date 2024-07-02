package com.example.springkotlinmvc.model.repository

import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.entity.ExampleGroupOneToMany
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface ExampleGroupOneToManyJpaRepository : JpaRepository<ExampleGroupOneToMany?, Long?>{
    companion object {
        fun fixture(name: String, examples: List<ExampleEntity>): ExampleGroupOneToMany {
            val group = ExampleGroupOneToMany(name = name)
            examples.forEach { group.addExample(it) }
            return group
        }
    }

    @Query("SELECT e FROM ExampleGroupOneToMany e JOIN FETCH e.examples")
    fun findAllWithExamples(): List<ExampleGroupOneToMany?>?
    }

