package com.example.springkotlinmvc.business

import com.example.springkotlinmvc.dto.ExampleUpdateRequest
import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.repository.ExampleJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExampleServiceTest @Autowired constructor(
    private val exampleService: ExampleService,
    private val exampleJpaRepository: ExampleJpaRepository
) {

    @AfterEach
    fun clean() {
        exampleJpaRepository.deleteAll()
    }

    @Test
    @DisplayName("Example 저장이 정상 동작한다")
    fun saveExampleTest() {
        // given
        val title = "title"
        val description = "description"

        // when
        exampleService.saveExample(title, description)

        // then
        val examples = exampleJpaRepository.findAll()
        assertEquals(1, examples.size)
        assertEquals(title, examples[0].title)
        assertEquals(description, examples[0].description)
    }

    @Test
    @DisplayName("Example 조회가 정상 동작한다")
    fun getExampleListTest() {
        // given
        exampleJpaRepository.saveAll(listOf(
            ExampleEntity(title = "title1", description = "description1"),
            ExampleEntity(title = "title2", description = "description2")
        ))

        // when
        val examples = exampleService.getExampleList()

        // then
        assertEquals(2, examples.size)
        assertEquals("title1", examples[0].title)
        assertEquals("description1", examples[0].description)
        assertEquals("title2", examples[1].title)
        assertEquals("description2", examples[1].description)
    }

    @Test
    @DisplayName("Example 제목으로 조회가 정상 동작한다")
    fun findExamplesByTitleTest() {
        // given
        exampleJpaRepository.saveAll(listOf(
            ExampleEntity(title = "title1", description = "description1"),
            ExampleEntity(title = "title2", description = "description2")
        ))

        // when
        val examples = exampleService.findExamplesByTitle("title")

        // then
        assertEquals(2, examples.size)
        assertEquals("title1", examples[0].title)
        assertEquals("description1", examples[0].description)
        assertEquals("title2", examples[1].title)
        assertEquals("description2", examples[1].description)
    }

    @Test
    @DisplayName("Example ID로 조회가 정상 동작한다")
    fun getExampleByIdTest() {
        // given
        val savedExample = exampleJpaRepository.save(ExampleEntity(title = "title", description = "description"))

        // when
        val example = exampleService.getExampleById(savedExample.id!!)

        // then
        assertEquals("title", example.title)
        assertEquals("description", example.description)
    }

    @Test
    @DisplayName("Example 업데이트가 정상 동작한다")
    fun updateExampleTest() {
        // given
        exampleJpaRepository.save(ExampleEntity(title = "title", description = "description"))
        val savedExample = exampleJpaRepository.findAll()[0]
        val exampleUpdateRequest = ExampleUpdateRequest(id = savedExample.id!!, title = "new title", description = "new description")

        // when
        exampleService.updateExample(savedExample.id!!, exampleUpdateRequest)

        // then
        val updatedExample = exampleJpaRepository.findById(savedExample.id!!).get()
        assertEquals("new title", updatedExample.title)
        assertEquals("new description", updatedExample.description)
    }

    @Test
    @DisplayName("Example 삭제가 정상 동작한다")
    fun deleteExampleTest() {
        // given
        val savedExample = exampleJpaRepository.save(ExampleEntity(title = "title", description = "description"))

        // when
        exampleService.deleteExample(savedExample.id!!)

        // then
        assertFalse(exampleJpaRepository.findById(savedExample.id!!).isPresent)
    }
}