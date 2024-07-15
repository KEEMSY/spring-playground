package com.example.kotlin.model.adapter

import com.example.kotlin.business.domain.Example
import com.example.kotlin.dto.ExampleEntitySearch
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.exception.ExampleCreationException
import com.example.kotlin.model.repository.ExampleJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ExampleRepositoryAdapterTest @Autowired constructor(
    private val exampleRepositoryAdapter: ExampleRepositoryAdapter,
    private val exampleJpaRepository: ExampleJpaRepository,
) {
    @AfterEach
    fun cleanUp() {
        exampleJpaRepository.deleteAll()
    }

    @Test
    fun `Example 생성 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val inputExample = Example(title = exampleEntity.title, description = exampleEntity.description)

        // when
        val expectedExample = exampleRepositoryAdapter.save(inputExample)

        // then
        assertNotNull(expectedExample)
        assertEquals(inputExample.title, expectedExample.title)
        assertEquals(inputExample.description, expectedExample.description)
    }

    @Test
    fun `Example 생성 실패 테스트 - title이 null일 경우 ExampleCreationException이 발생한다-변환에서 에러 발생`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val inputExample = Example(title = null, description = exampleEntity.description)

        // when & then
        val exception = assertThrows<ExampleCreationException> {
            exampleRepositoryAdapter.save(inputExample)
        }

        assertEquals("Example을 생성하는데 실패 했습니다.", exception.message)
    }

    @Test
    fun `Example 조회 테스트 - 검색 Title을 포함하는 데이터를 조회한다`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleEntitySearch = ExampleEntitySearch(exampleTitle = exampleEntity.title)

        // when
        val expectedExample = exampleRepositoryAdapter.getByCriteria(exampleEntitySearch)

        // then
        assertNotNull(expectedExample)
        assertEquals(exampleEntitySearch.exampleTitle, expectedExample[0].title)
        assertEquals(exampleEntity.description, expectedExample[0].description)
    }

}