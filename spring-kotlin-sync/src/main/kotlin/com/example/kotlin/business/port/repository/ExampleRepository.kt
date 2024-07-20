package com.example.kotlin.business.port.repository

import com.example.kotlin.business.domain.Example
import com.example.kotlin.dto.ExampleEntitySearch


interface ExampleRepository {
    /**
     DB Port(DBInterface)
     * 작성하는 메서드 명은 DB CRUD에 맞춰 작성한다.
     */
    fun create(example: Example): Example
    fun readExampleListByCriteria(exampleEntitySearch: ExampleEntitySearch): List<Example>
    fun readExampleById(id: Long): Example?
    fun update(exampleId: Long, example: Example): Example
    fun delete(id: Long)
}