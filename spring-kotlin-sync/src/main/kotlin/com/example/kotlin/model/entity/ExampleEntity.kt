package com.example.kotlin.model.entity

import jakarta.persistence.*

@Entity
data class ExampleEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var description: String,

    @ManyToOne
    @JoinColumn(name = "group_id")
    var exampleGroup: ExampleGroupOneToMany? = null
) {
    fun updateTitle(title: String) {
        this.title = title
    }
    fun updateDescription(description: String) {
        this.description = description
    }
}