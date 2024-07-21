package com.example.kotlin.dto

data class ExampleUpdateRequest(
    val id: Long,
    val title: String? = null,
    val description: String? = null
)
