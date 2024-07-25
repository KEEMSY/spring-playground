package com.example.kotlin.business

import com.example.kotlin.business.domain.Example
import com.example.kotlin.business.port.repository.ExampleRepository
import com.example.kotlin.dto.ExampleSearch
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ExampleService (private val exampleRepository: ExampleRepository){
    /**
     * Service
     * 작성하는 메서드 명은 비즈니스 로직에 맞춰 작성한다.
     * - 비즈니스 로직을 포함한다.
     * - 비즈니스 이해 관계자들이 이해할 수 있는 용어를 사용한다.
     * - DB 동작(CRUD)를 포함하지 않는다.
     *
     * 추후 추가할 사항
     * - Logger
     */
    fun save(example: Example): Example {
        return exampleRepository.create(example)
    }

    fun getByCriteria(exampleSearch: ExampleSearch, pageable: Pageable): List<Example> {
        return exampleRepository.readExampleListByCriteria(exampleSearch, pageable)
    }

    fun getById(id: Long): Example? {
        return exampleRepository.readExampleById(id)
    }

    fun modify(example: Example): Example {
        return exampleRepository.update(example)
    }

    fun remove(id: Long) {
        exampleRepository.delete(id)
    }
}