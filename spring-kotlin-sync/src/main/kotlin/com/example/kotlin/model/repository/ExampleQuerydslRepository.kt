package com.example.kotlin.model.repository

import kotlinx.coroutines.*

import com.example.kotlin.dto.ExampleDTOByUsingQueryProjection
import com.example.kotlin.dto.ExampleSearch
import com.example.kotlin.dto.QExampleDTOByUsingQueryProjection
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.entity.QExampleEntity.exampleEntity
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
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

    fun readExampleBy(exampleSearch: ExampleSearch, pageable: Pageable): List<ExampleEntity> {
        return queryFactory
            .selectFrom(exampleEntity)
            .where(
                titleContains(exampleSearch.exampleTitle),
                descriptionContains(exampleSearch.exampleDescription)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    fun fetchOrdersWithPagingByFetchResultsWithOptimizeCountQuery(
        exampleSearch: ExampleSearch,
        pageable: Pageable
    ): Page<ExampleDTOByUsingQueryProjection> {
        val content = queryFactory
            .select(
                QExampleDTOByUsingQueryProjection(
                    exampleEntity.title!!,
                    exampleEntity.description!!
                    )
                )
            .from(exampleEntity)
            .where(
                titleContains(exampleSearch.exampleTitle),
                descriptionContains(exampleSearch.exampleDescription)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = queryFactory
            .select(exampleEntity.count())
            .from(exampleEntity)
            .where(
                titleContains(exampleSearch.exampleTitle),
                descriptionContains(exampleSearch.exampleDescription)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    suspend fun fetchOrdersWithPagingByFetchResultsWithOptimizeCountQueryWithCoroutines(
        exampleSearch: ExampleSearch,
        pageable: Pageable
    ): Page<ExampleDTOByUsingQueryProjection> = withContext(Dispatchers.IO) {
        val contentDeferred = async {
            queryFactory
                .select(
                    QExampleDTOByUsingQueryProjection(
                        exampleEntity.title,
                        exampleEntity.description
                    )
                )
                .from(exampleEntity)
                .where(
                    titleContains(exampleSearch.exampleTitle),
                    descriptionContains(exampleSearch.exampleDescription)
                )
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()
        }

        val countQueryDeferred = async {
            queryFactory
                .select(exampleEntity.count())
                .from(exampleEntity)
                .where(
                    titleContains(exampleSearch.exampleTitle),
                    descriptionContains(exampleSearch.exampleDescription)
                )
                .fetchOne() ?: 0L
        }

        val content = contentDeferred.await()
        val totalCount = countQueryDeferred.await()

        PageableExecutionUtils.getPage(content, pageable) { totalCount }
    }

    private fun descriptionContains(exampleDescription: String?): BooleanExpression? {
        return exampleDescription?.let { exampleEntity.description.eq(it) }
    }

    private fun titleContains(exampleName: String?): BooleanExpression? {
        return exampleName?.let { exampleEntity.title.eq(it) }
    }

}