package com.example.kotlin.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "example_entity")
data class ExampleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var exampleGroup: ExampleGroupOneToMany? = null
) {
    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    override fun toString(): String {
        return "ExampleEntity(id=$id, title='$title', description='$description')"
    }

}