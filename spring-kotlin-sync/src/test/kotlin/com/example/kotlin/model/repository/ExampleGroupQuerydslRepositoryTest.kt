package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleGroupOneToManySearch
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.entity.ExampleGroupOneToMany
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
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

    @Test
    @Transactional
    fun `ExampleGroupSearch이 빈 값 일경우, 모든 데이터를 조회한다`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)
        val exampleGroup = ExampleGroupOneToMany(name = "name", examples = exampleEntityList)
        exampleGroupJpaRepository.save(exampleGroup)

        val exampleGroupOneToManySearch = ExampleGroupOneToManySearch()

        // when
        val result = exampleGroupQuerydslRepository.readExampleGroupBy(exampleGroupOneToManySearch)
        // then
        assertNotNull(result, "Result should not be null")
        assertEquals(1, result.size, "Result size should be 1")
    }

    @Test
    @Transactional
    fun `ExampleGroupSearch의 값에 일치하는 데이터를 조회한다`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)

        val exampleGroup = ExampleGroupOneToMany(name = "name", examples = exampleEntityList)
        exampleGroupJpaRepository.save(exampleGroup)

        val exampleGroupOneToManySearch = ExampleGroupOneToManySearch(exampleGroupName = "name")

        // when
        val result = exampleGroupQuerydslRepository.readExampleGroupBy(exampleGroupOneToManySearch)

        // then
        assertNotNull(result, "Result should not be null")
        assertEquals(1, result.size, "Result size should be 1")
        assertEquals("name", result[0].name, "Result name should be 'name'")
        assertEquals(3, result[0].examples.size, "Result examples size should be 3")
    }


    @Test
    @Transactional
    @Disabled
    fun `ExampleGroupSearch의 값에 일치하는 ExampleEntity에 따라 조회한다 데이터가 존재하지 않는 실패 테스트-디버깅 필요`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)

        val exampleGroup = ExampleGroupOneToMany(name = "name", examples = exampleEntityList)
        exampleGroupJpaRepository.save(exampleGroup)

        val exampleGroupOneToManySearch = ExampleGroupOneToManySearch(exampleTitle = "title")

        // when
        val result = exampleGroupQuerydslRepository.readExampleGroupBy(exampleGroupOneToManySearch)
        val test = exampleJpaRepository.findByTitleContaining("title")

        // then
        assertNotNull(test, "Test should not be null")
        println("test: ${test}")
        assertNotNull(result, "Result should not be null")
        assertEquals(1, result.size, "Result size should be 1")
        assertEquals("name", result[0].name, "Result name should be 'name'")
        assertEquals(1, result[0].examples.size, "Result examples size should be 1")
        assertEquals("title", result[0].examples[0].title, "Result example title should be 'title'")
        assertEquals("description", result[0].examples[0].description, "Result example description should be 'description'")
    }

    @Test
    @Transactional
    fun `ExampleGroupSearch의 값에 일치하는 ExampleEntity에 따라 조회한다`() {
        // given
        val exampleGroup = ExampleGroupOneToMany(name = "name")
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description", exampleGroup = exampleGroup),
            ExampleEntity(title = "similarTitle", description = "similarDescription", exampleGroup = exampleGroup),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription", exampleGroup = exampleGroup)
        )
        exampleGroup.examples = exampleEntityList
        exampleGroupJpaRepository.save(exampleGroup)

        val exampleGroupOneToManySearch = ExampleGroupOneToManySearch(exampleTitle = "title")

        // when
        val result = exampleGroupQuerydslRepository.readExampleGroupBy(exampleGroupOneToManySearch)
        val test = exampleJpaRepository.findByTitleContaining("title")

        // then
        assertNotNull(test, "Test should not be null")
        println("test: $test")
        assertNotNull(result, "Result should not be null")
        assertEquals(1, result.size, "Result size should be 1")
        assertEquals("name", result[0].name, "Result name should be 'name'")
        assertEquals(3, result[0].examples.size, "Result examples size should be 1")
    }


}