package com.example.kotlin.model.repository

import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.entity.QExampleEntity.exampleEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class ExampleQuerydslRepository(
    private val queryFactory: JPAQueryFactory
){
    fun readExampleList(): List<ExampleEntity> {
        return queryFactory
            .selectFrom(exampleEntity)
            .fetch()
    }
}