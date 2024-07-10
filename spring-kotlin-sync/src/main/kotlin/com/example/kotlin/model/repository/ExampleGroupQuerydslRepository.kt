package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleGroupOneToManySearch
import com.example.kotlin.model.entity.ExampleGroupOneToMany
import com.example.kotlin.model.entity.QExampleEntity.exampleEntity
import com.example.kotlin.model.entity.QExampleGroupOneToMany.exampleGroupOneToMany
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class ExampleGroupQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun readExampleGroupList(): List<ExampleGroupOneToMany> {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .fetch()
    }

    fun readExampleGroupById(id: Long): ExampleGroupOneToMany? {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .where(exampleGroupOneToMany.id.eq(id))
            .fetchOne()
    }

    fun readExampleGroupBy(exampleGroupOneToManySearch: ExampleGroupOneToManySearch): List<ExampleGroupOneToMany> {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .where(
                nameContains(exampleGroupOneToManySearch.exampleGroupName),
                exampleTitleContains(exampleGroupOneToManySearch.exampleTitle)
            )
            .fetch()
    }

    private fun exampleTitleContains(exampleTitle: String?): BooleanExpression {
        return exampleTitle?.let {
            JPAExpressions.selectFrom(exampleEntity)
                .where(
                    exampleEntity.title.eq(it)
                    .and(exampleEntity.exampleGroup.id.eq(exampleGroupOneToMany.id))
                )
                .exists()
        } ?: exampleGroupOneToMany.isNotNull
    }

    private fun nameContains(exampleGroupTitle: String?): BooleanExpression {
        return exampleGroupTitle?.let { exampleGroupOneToMany.name.eq(it) } ?: exampleGroupOneToMany.name.isNotNull
    }

}