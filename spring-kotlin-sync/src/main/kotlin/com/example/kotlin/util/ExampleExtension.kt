package com.example.kotlin.util

import com.example.kotlin.business.domain.Example
import com.example.kotlin.model.entity.ExampleEntity

fun Example.toEntity(): ExampleEntity {
    return ExampleEntity(
        id = id,
        title = title ?: throw IllegalArgumentException("제목은 null이 될 수 없습니다."),
        description = description ?: throw IllegalArgumentException("설명은 null이 될 수 없습니다.")
    )
}

fun ExampleEntity.toDomain(): Example {
    return Example(
        id = id,
        title = title,
        description = description
    )
}
