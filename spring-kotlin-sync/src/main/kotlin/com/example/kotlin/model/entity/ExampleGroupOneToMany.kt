package com.example.kotlin.model.entity

import jakarta.persistence.*

@Entity
data class ExampleGroupOneToMany(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var name: String, // var로 변경하여 변경 가능하도록 수정

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "exampleGroup")
    val examples: MutableList<ExampleEntity> = mutableListOf()
) {
    fun addExample(example: ExampleEntity) {
        examples.add(example)
        example.exampleGroup = this // example의 그룹 설정
    }

    fun removeExample(example: ExampleEntity) {
        examples.remove(example)
        example.exampleGroup = null // example의 그룹 해제
    }

    fun updateName(name: String) {
        this.name = name // 이름 변경 가능하도록 수정
    }
}