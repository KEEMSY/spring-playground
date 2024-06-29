package com.example.springkotlinmvc.dto

data class ExampleUpdateRequest (
    val id: Long,
    val title: String?,
    val description: String?
)