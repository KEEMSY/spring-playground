package com.example.kotlin.business.port.repository

import com.example.kotlin.business.domain.Example


interface ExampleRepository {
    fun save(example: Example): Example
    fun getByTitleContaining(title: String): List<Example>
    fun getById(id: Long): Example?
    fun getAll(): List<Example>
    fun modify(exampleEntity: Example): Example
    fun remove(id: Long)
}