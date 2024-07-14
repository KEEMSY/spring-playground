package com.example.kotlin.business.domain

import com.example.kotlin.model.entity.ExampleEntity

data class Example
    (
    val title: String? = null,
    val description: String? = null
    ) {
    fun toEntity(): ExampleEntity {
        return ExampleEntity(
            title = title!!,
            description = description!!
        )
    }
}