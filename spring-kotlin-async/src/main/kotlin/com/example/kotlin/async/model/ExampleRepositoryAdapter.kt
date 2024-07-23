package com.example.kotlin.async.model

import com.example.kotlin.async.business.domain.Example
import com.example.kotlin.async.business.port.repository.ExampleRepository
import com.example.kotlin.async.dto.ExampleSearch
import com.example.kotlin.async.model.repository.ExampleR2dbcRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ExampleRepositoryAdapter (
  private val exampleR2dbcRepository: ExampleR2dbcRepository
): ExampleRepository {
    override fun create(example: Example): Example {
        TODO("Not yet implemented")
    }

    override fun readExampleListByCriteria(exampleSearch: ExampleSearch, pageable: Pageable): List<Example> {
        TODO("Not yet implemented")
    }

    override fun readExampleById(id: Long): Example? {
        TODO("Not yet implemented")
    }

    override fun update(example: Example): Example {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}