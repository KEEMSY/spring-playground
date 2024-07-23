package com.example.kotlin.async.business.port.repository

import com.example.kotlin.async.business.domain.Example
import com.example.kotlin.async.dto.ExampleSearch
import org.springframework.data.domain.Pageable

interface ExampleRepository {
    /**
    DB Port(DBInterface)
     * 작성하는 메서드 명은 DB CRUD에 맞춰 작성한다.
     */
    fun create(example: Example): Example
    fun readExampleListByCriteria(exampleSearch: ExampleSearch, pageable: Pageable): List<Example>
    fun readExampleById(id: Long): Example?
    fun update(example: Example): Example
    fun delete(id: Long)
}