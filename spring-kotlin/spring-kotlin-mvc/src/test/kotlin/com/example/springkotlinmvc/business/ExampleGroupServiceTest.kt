package com.example.springkotlinmvc.business

import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.repository.ExampleGroupOneToManyJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class ExampleGroupServiceTest @Autowired constructor(
    private val exampleGroupService: ExampleGroupService,
    private val exampleGroupOneToManyJpaRepository: ExampleGroupOneToManyJpaRepository
) {
    @AfterEach
    fun clean() {
        exampleGroupOneToManyJpaRepository.deleteAll()
    }

    @Test
    @DisplayName("ExampleGroup 저장이 정상 동작한다")
    fun saveExampleGroupTest() {
        // given
        val name = "name"
        val examples = listOf(
            ExampleEntity(title = "title1", description = "description1"),
            ExampleEntity(title = "title2", description = "description2")
        )

        // when
        exampleGroupService.saveExampleGroup(name, examples)

        // then
        val exampleGroups = exampleGroupOneToManyJpaRepository.findAllWithExamples()
        assertEquals(1, exampleGroups!!.size)
        assertEquals(name, exampleGroups[0]!!.name)
        assertEquals(2, exampleGroups[0]!!.examples.size)
        assertEquals("title1", exampleGroups[0]!!.examples[0].title)
        assertEquals("description1", exampleGroups[0]!!.examples[0].description)
        assertEquals("title2", exampleGroups[0]!!.examples[1].title)
        assertEquals("description2", exampleGroups[0]!!.examples[1].description)
    }

    @Test
    @DisplayName("ExampleGroup 조회가 정상 동작한다")
    @Transactional
    fun getExampleGroupListTest() {
        // given
        exampleGroupOneToManyJpaRepository.saveAll(listOf(
            ExampleGroupOneToManyJpaRepository.fixture("name1", listOf(
                ExampleEntity(title = "title1", description = "description1"),
                ExampleEntity(title = "title2", description = "description2")
            )),
            ExampleGroupOneToManyJpaRepository.fixture("name2", listOf(
                ExampleEntity(title = "title3", description = "description3"),
                ExampleEntity(title = "title4", description = "description4")
            ))
        ))

        // when
        val exampleGroups = exampleGroupService.getExampleGroupList()

        // then
        assertEquals(2, exampleGroups.size)
        assertEquals("name1", exampleGroups[0].name)
        assertEquals(2, exampleGroups[0].examples.size)
        assertEquals("title1", exampleGroups[0].examples[0].title)
        assertEquals("description1", exampleGroups[0].examples[0].description)
        assertEquals("title2", exampleGroups[0].examples[1].title)
        assertEquals("description2", exampleGroups[0].examples[1].description)
        assertEquals("name2", exampleGroups[1].name)
        assertEquals(2, exampleGroups[1].examples.size)
        assertEquals("title3", exampleGroups[1].examples[0].title)
        assertEquals("description3", exampleGroups[1].examples[0].description)
        assertEquals("title4", exampleGroups[1].examples[1].title)
        assertEquals("description4", exampleGroups[1].examples[1].description)
    }
}