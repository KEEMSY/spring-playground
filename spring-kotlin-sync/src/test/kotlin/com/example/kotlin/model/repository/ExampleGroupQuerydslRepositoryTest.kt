package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleGroupOneToManySearch
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.entity.ExampleGroupOneToMany
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class ExampleGroupQuerydslRepositoryTest @Autowired constructor(
    private val exampleGroupQuerydslRepository: ExampleGroupQuerydslRepository,
    private val exampleGroupJpaRepository: ExampleGroupOneToManyJpaRepository,
    private val exampleJpaRepository: ExampleJpaRepository
) {

    @AfterEach
    fun cleanUp() {
        exampleGroupJpaRepository.deleteAll()
        exampleJpaRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `ExampleGroup 조회 테스트`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)

        val exampleGroup = ExampleGroupOneToMany(name = "name", examples = exampleEntityList)
        exampleGroupJpaRepository.save(exampleGroup)

        // when
        val exampleGroupList = exampleGroupQuerydslRepository.readExampleGroupList()
        val actualExampleGroup = exampleGroupList[0]

        // then
        assertNotNull(exampleGroupList, "Example group list should not be null")
        assertTrue(exampleGroupList.isNotEmpty(), "Example group list should not be empty")
        assertEquals(1, exampleGroupList.size, "Example group list size should be 1")
        assertEquals("name", actualExampleGroup.name, "Example group name should be 'name'")
        assertEquals(3, actualExampleGroup.examples.size, "Example group should have 3 examples")
    }

    @Test
    @Transactional
    fun `ExampleGroup id로 조회 테스트`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)

        val exampleGroup = ExampleGroupOneToMany(name = "name", examples = exampleEntityList)
        exampleGroupJpaRepository.save(exampleGroup)

        // when
        val result = exampleGroup.id?.let { exampleGroupQuerydslRepository.readExampleGroupById(it) }

        // then
        assertNotNull(result, "Result should not be null")
        assertEquals(exampleGroup.id, result?.id, "Result id should be equal to example group id")
    }

}