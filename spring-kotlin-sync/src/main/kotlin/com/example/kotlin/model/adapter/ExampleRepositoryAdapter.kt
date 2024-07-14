package com.example.kotlin.model.adapter

import com.example.kotlin.business.domain.Example
import com.example.kotlin.business.port.repository.ExampleRepository
import com.example.kotlin.model.exception.ExampleCreationException
import com.example.kotlin.model.repository.ExampleJpaRepository
import com.example.kotlin.model.repository.ExampleQuerydslRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExampleRepositoryAdapter (
    private val exampleJpaRepository: ExampleJpaRepository,
    private val exampleQuerydslRepository: ExampleQuerydslRepository
) : ExampleRepository {

    @Transactional
    override fun save(example: Example): Example {
        return try {
            val exampleEntity = example.toEntity()
            val savedExampleEntity = exampleJpaRepository.save(exampleEntity)
            savedExampleEntity.toDomain()
        } catch (e: Exception) {
            throw ExampleCreationException("Example을 생성하는데 실패 했습니다.", e)
        }
    }

    override fun getByTitleContaining(title: String): List<Example> {
        TODO("Not yet implemented")
    }

    override fun getById(id: Long): Example? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Example> {
        TODO("Not yet implemented")
    }

    override fun modify(exampleEntity: Example): Example {
        TODO("Not yet implemented")
    }

    override fun remove(id: Long) {
        TODO("Not yet implemented")
    }
}