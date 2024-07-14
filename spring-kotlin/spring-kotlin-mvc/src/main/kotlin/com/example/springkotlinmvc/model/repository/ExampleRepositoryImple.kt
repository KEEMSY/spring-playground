package com.example.springkotlinmvc.model.repository

import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.entity.QExampleEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class ExampleRepositoryImpl(
    @PersistenceContext
    private val queryFactory: JPAQueryFactory
): ExampleRepository {

    override fun findAll(): List<ExampleEntity> {
        return queryFactory.select(QExampleEntity.exampleEntity)
            .from(QExampleEntity.exampleEntity)
            .fetch()
    }

    override fun findById(id: Long): ExampleEntity? {
        TODO("Not yet implemented")
    }

    override fun save(example: ExampleEntity): ExampleEntity {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }
}