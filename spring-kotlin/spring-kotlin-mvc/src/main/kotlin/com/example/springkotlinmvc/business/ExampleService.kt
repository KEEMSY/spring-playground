package com.example.springkotlinmvc.business

import com.example.springkotlinmvc.dto.ExampleUpdateRequest
import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.repository.ExampleJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExampleService(private val exampleJpaRepository: ExampleJpaRepository) {

    @Transactional
    fun saveExample(title: String, description: String) {
        exampleJpaRepository.save(ExampleEntity(title = title, description = description))
    }

    @Transactional(readOnly = true)
    fun getExampleList(): List<ExampleEntity> {
        return exampleJpaRepository.findAll()
    }

    fun findExamplesByTitle(title: String): List<ExampleEntity> {
        return exampleJpaRepository.findByTitleContaining(title)
    }

    @Transactional(readOnly = true)
    fun getExampleById(id: Long): ExampleEntity {
        return exampleJpaRepository.findById(id).orElseThrow { throw IllegalArgumentException("Example not found") }
    }

    @Transactional
    fun updateExample(id: Long, exampleUpdateRequest: ExampleUpdateRequest) {
        val existingExample: ExampleEntity = exampleJpaRepository.findById(id).orElseThrow {
            NoSuchElementException("Example not found with id $id")
        }

        exampleUpdateRequest.title?.let {
            existingExample.updateTitle(it)
        }

        exampleUpdateRequest.description?.let {
            existingExample.updateDescription(it)
        }

        exampleJpaRepository.save(existingExample)
    }

    @Transactional
    fun deleteExample(id: Long) {
        exampleJpaRepository.deleteById(id)
    }
}