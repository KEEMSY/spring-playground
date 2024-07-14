package com.example.springkotlinmvc.model.repository

import com.example.springkotlinmvc.model.entity.ExampleEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExampleRepositoryImplTest @Autowired constructor(
    private val exampleRepository: ExampleRepository,
    private val exampleJpaRepository: ExampleJpaRepository
) {

    @AfterEach
    fun clean() {
        exampleJpaRepository.deleteAll()
    }

    @Test
    fun testSaveAndFindById() {
        // given
        val title = "title"
        val description = "description"

        // when
        val savedExample = exampleRepository.save(ExampleEntity(title = title, description = description))

        // then
        assertNotNull(savedExample.id)
        assertEquals(title, savedExample.title)
        assertEquals(description, savedExample.description)

        // when
        val foundExample = exampleRepository.findById(savedExample.id!!)

        // then
        assertNotNull(foundExample)
        assertEquals(savedExample.id, foundExample?.id)
        assertEquals(title, foundExample?.title)
        assertEquals(description, foundExample?.description)
    }

}