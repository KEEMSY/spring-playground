package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleEntitySearch
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

    @Test
    fun `ExampleEntity id로 조회 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        print("exampleEntity: ${exampleEntity}")
        exampleJpaRepository.save(exampleEntity)

        // when
        val result = exampleEntity.id?.let { exampleQuerydslRepository.readExampleById(it) }
        println("result: ${result}")
        // then
        assertNotNull(result)
        assertEquals(exampleEntity.id, result?.id)
    }

    @Test
    fun `ExampleSearch이 빈 값 일경우, 모든 데이터를 조회한다`() {
        // given
        val exampleEntities = listOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )

        exampleJpaRepository.saveAll(exampleEntities)

        val exampleEntitySearch = ExampleEntitySearch()

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleEntitySearch)

        // then
        assertNotNull(result)
        assertEquals(exampleEntities.size, result.size)
    }

    @Test
    fun `ExampleEntitySearch로 title이 일치하는 데이터만 조회 한다`() {
        // given
        val exampleEntities = listOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )

        exampleJpaRepository.saveAll(exampleEntities)


        val exampleEntitySearch = ExampleEntitySearch(exampleTitle = "title")

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleEntitySearch)

        // then
        assertNotNull(result)

        val count = result.size
        assertEquals(1, count)
        assertEquals(exampleEntities[0].title, result[0].title)
        assertEquals(exampleEntities[0].description, result[0].description)
    }

    @Test
fun `ExampleEntitySearch로 description이 일치하는 데이터가 없을 경우 빈 리스트를 반환한다`() {
        // given
        val exampleEntities = listOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )

        exampleJpaRepository.saveAll(exampleEntities)

        val exampleEntitySearch = ExampleEntitySearch(exampleDescription = "notExistDescription")

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleEntitySearch)
        println("result: ${result}")

        // then
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }
}