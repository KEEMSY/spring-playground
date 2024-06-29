package com.example.springkotlinmvc.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class ExampleEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var description: String
) {
    fun updateTitle(title: String) {
        this.title = title
    }
    fun updateDescription(description: String) {
        this.description = description
    }
}