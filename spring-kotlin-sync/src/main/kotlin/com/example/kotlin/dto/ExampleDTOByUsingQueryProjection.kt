package com.example.kotlin.dto

import com.querydsl.core.annotations.QueryProjection

data class ExampleDTOByUsingQueryProjection @QueryProjection constructor(
    val exampleTitle: String,
    val exampleDescription: String
)
