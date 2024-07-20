package com.example.kotlin.model.adapter

import com.example.kotlin.business.domain.Example
import com.example.kotlin.business.port.repository.ExampleRepository
import com.example.kotlin.dto.ExampleEntitySearch
import com.example.kotlin.model.exception.ExampleCreationException
import com.example.kotlin.model.exception.ExampleSearchException
import com.example.kotlin.model.repository.ExampleJpaRepository
import com.example.kotlin.model.repository.ExampleQuerydslRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExampleRepositoryAdapter(
    private val exampleJpaRepository: ExampleJpaRepository,
    private val exampleQuerydslRepository: ExampleQuerydslRepository
) : ExampleRepository {

    @Transactional
    override fun create(example: Example): Example {
        return try {
            val exampleEntity = example.toEntity()
            val savedExampleEntity = exampleJpaRepository.save(exampleEntity)
            savedExampleEntity.toDomain()
        } catch (e: Exception) {
            throw ExampleCreationException("Example을 생성하는데 실패 했습니다.", e)
        }
    }

    override fun readExampleListByCriteria(exampleEntitySearch: ExampleEntitySearch): List<Example> {
        return try {
            val entityList = exampleQuerydslRepository.readExampleBy(exampleEntitySearch)
            val exampleList = entityList.map { it.toDomain() }
            exampleList
        } catch (e: Exception) {
            throw ExampleSearchException("조회 과정에서 알수 없는 에러가 발생 했습니다.", e)
        }
    }

    override fun readExampleById(id: Long): Example? {
        return try {
            val entity = exampleQuerydslRepository.readExampleById(id)
            entity?.toDomain()
        } catch (e: Exception) {
            throw ExampleSearchException("조회 과정에서 알수 없는 에러가 발생 했습니다.", e)
        }
    }

    @Transactional
    override fun update(exampleId: Long, example: Example): Example {
        return try {
            val entity = exampleJpaRepository.findById(exampleId)
                .orElseThrow { ExampleSearchException("수정할 데이터를 찾을 수 없습니다.") }

            entity.title = example.title ?: throw IllegalArgumentException("제목은 null이 될 수 없습니다.")
            entity.description = example.description ?: throw IllegalArgumentException("설명은 null이 될 수 없습니다.")

            val savedEntity = exampleJpaRepository.save(entity)
            savedEntity.toDomain()
        } catch (e: Exception) {
            when (e) {
                is ExampleSearchException, is IllegalArgumentException -> throw e
                else -> throw ExampleSearchException("수정 과정에서 알 수 없는 에러가 발생했습니다.", e)
            }
        }
    }

    override fun delete(id: Long) {
        try {
            val exists = exampleJpaRepository.existsById(id)
            if (!exists) {
                throw ExampleSearchException("삭제할 데이터를 찾을 수 없습니다.")
            }
            exampleJpaRepository.deleteById(id)
        } catch (e: Exception) {
            when (e) {
                is ExampleSearchException, is IllegalArgumentException -> throw e
                else -> throw ExampleSearchException("삭제 과정에서 알 수 없는 에러가 발생했습니다.", e)
            }
        }
    }
}