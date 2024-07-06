package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleEntitySearch
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.entity.QExampleEntity.exampleEntity
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class ExampleQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun readExampleList(): List<ExampleEntity> {
        return queryFactory
            .selectFrom(exampleEntity)
            .fetch()
    }

    fun readExampleById(id: Long): ExampleEntity? {
        return queryFactory
            .selectFrom(exampleEntity)
            .where(exampleEntity.id.eq(id))
            .fetchOne()
    }

    fun readExampleBy(exampleEntitySearch: ExampleEntitySearch): ExampleEntity? {
        return queryFactory
            .selectFrom(exampleEntity)
            .where(
                titleContains(exampleEntitySearch.exampleTitle),
                descriptionContains(exampleEntitySearch.exampleDescription)
            )
            .fetchOne()
    }

    private fun descriptionContains(exampleDescription: String?): BooleanExpression? {
        return exampleDescription?.let { exampleEntity.description.eq(it) }
    }

    private fun titleContains(exampleName: String?): BooleanExpression? {
        return exampleName?.let { exampleEntity.title.eq(it) }
    }

}