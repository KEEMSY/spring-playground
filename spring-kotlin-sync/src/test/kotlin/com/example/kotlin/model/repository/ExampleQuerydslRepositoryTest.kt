package com.example.kotlin.model.repository

import com.example.kotlin.dto.ExampleSearch
import com.example.kotlin.model.entity.ExampleEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

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

        val exampleSearch = ExampleSearch()

        val page = 0;
        val size = 10;
        val pageable = PageRequest.of(page, size);

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleSearch=exampleSearch, pageable=pageable)

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


        val exampleSearch = ExampleSearch(exampleTitle = "title")

        val page = 0;
        val size = 10;
        val pageable = PageRequest.of(page, size);

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleSearch=exampleSearch, pageable=pageable)

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

        val exampleSearch = ExampleSearch(exampleDescription = "notExistDescription")

        val page = 0;
        val size = 10;
        val pageable = PageRequest.of(page, size);

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleSearch=exampleSearch, pageable=pageable)
        println("result: ${result}")

        // then
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `페이지네이션이 적용된 경우, 해당 페이지의 데이터를 반환한다`() {
        // given
        val exampleEntities = listOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntities)

        val exampleSearch = ExampleSearch()
        val pageable = PageRequest.of(0, 2)

        // when
        val result = exampleQuerydslRepository.readExampleBy(exampleSearch = exampleSearch, pageable = pageable)

        // then
        assertNotNull(result)
        assertEquals(2, result.size)
    }
}