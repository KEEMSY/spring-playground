package com.example.springkotlinmvc.model.repository

import com.example.springkotlinmvc.model.entity.ExampleEntity

interface ExampleRepository {
    fun findAll(): List<ExampleEntity>
    fun findById(id: Long): ExampleEntity?
    fun save(example: ExampleEntity): ExampleEntity
    fun deleteById(id: Long)
}