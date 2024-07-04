package com.example.kotlin.model.repository

import com.example.kotlin.model.entity.ExampleEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExampleQuerydslRepositoryTest @Autowired constructor(
    private val exampleQuerydslRepository: ExampleQuerydslRepository,
    private val exampleJpaRepository: ExampleJpaRepository
) {

    @AfterEach
    fun cleanUp() {
        exampleJpaRepository.deleteAll()
    }

    @Test
    fun `ExampleEntity 조회 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)

        // when
        val exampleList = exampleQuerydslRepository.readExampleList()

        // then
        assertNotNull(exampleList)
        assertTrue(exampleList.isNotEmpty())
    }
}