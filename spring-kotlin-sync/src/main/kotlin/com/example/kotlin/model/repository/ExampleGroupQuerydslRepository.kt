package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleGroupOneToManySearch
import com.example.kotlin.model.entity.ExampleGroupOneToMany
import com.example.kotlin.model.entity.QExampleEntity.exampleEntity
import com.example.kotlin.model.entity.QExampleGroupOneToMany.exampleGroupOneToMany
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
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


    // fetch join을 사용하여 연관된 엔티티를 함께 조회하는 메서드
    fun readExampleGroupWithExamples(): List<ExampleGroupOneToMany> {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .leftJoin(exampleGroupOneToMany.examples).fetchJoin()
            .fetch()
    }

    fun readExampleGroupByIdWithExamples(id: Long): ExampleGroupOneToMany? {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .leftJoin(exampleGroupOneToMany.examples).fetchJoin()
            .where(exampleGroupOneToMany.id.eq(id))
            .fetchOne()
    }

    fun readExampleGroupWithExamplesBy(exampleGroupOneToManySearch: ExampleGroupOneToManySearch, pageable: Pageable): List<ExampleGroupOneToMany> {
        return queryFactory
            .selectFrom(exampleGroupOneToMany)
            .leftJoin(exampleGroupOneToMany.examples).fetchJoin()
            .where(
                nameContains(exampleGroupOneToManySearch.exampleGroupName),
                exampleTitleContains(exampleGroupOneToManySearch.exampleTitle)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    fun readExampleGroupWithExamplesByFetchResultsWithOptimizeCountQuery(
        exampleGroupOneToManySearch: ExampleGroupOneToManySearch,
        pageable: Pageable
    ): Page<ExampleGroupOneToMany> {
        val content = queryFactory
            .selectFrom(exampleGroupOneToMany)
            .leftJoin(exampleGroupOneToMany.examples).fetchJoin()
            .where(
                nameContains(exampleGroupOneToManySearch.exampleGroupName),
                exampleTitleContains(exampleGroupOneToManySearch.exampleTitle)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(exampleGroupOneToMany.count())
            .from(exampleGroupOneToMany)
            .where(
                nameContains(exampleGroupOneToManySearch.exampleGroupName),
                exampleTitleContains(exampleGroupOneToManySearch.exampleTitle)
            )
            .fetchOne() ?: 0L
        return PageableExecutionUtils.getPage(content, pageable) { countQuery }
    }

    // Querydsl을 사용하여 검색 조건을 생성하는 메서드
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