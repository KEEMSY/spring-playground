package com.example.kotlin.model.adapter

import com.example.kotlin.business.domain.Example
import com.example.kotlin.dto.ExampleEntitySearch
import com.example.kotlin.model.entity.ExampleEntity
import com.example.kotlin.model.exception.ExampleCreationException
import com.example.kotlin.model.exception.ExampleSearchException
import com.example.kotlin.model.repository.ExampleJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

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

    @Test
    fun `Example 조회 테스트 - 데이터가 존재하지 않을 경우 빈 리스트를 반환한다`()
    {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleEntitySearch = ExampleEntitySearch(exampleTitle = "notExistTitle")

        // when
        val expectedExample = exampleRepositoryAdapter.getByCriteria(exampleEntitySearch)

        // then
        assertNotNull(expectedExample)
        assertTrue(expectedExample.isEmpty())
    }

    @Test
    fun `id를 사용하여 Example 조회 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)

        // when
        val expectedExample = exampleRepositoryAdapter.getById(exampleEntity.id!!)

        // then
        assertNotNull(expectedExample)
        assertEquals(exampleEntity.title, expectedExample?.title)
        assertEquals(exampleEntity.description, expectedExample?.description)
    }

    @Test
    fun `id를 사용하여 Example 조회 테스트 - 데이터가 존재하지 않을 경우 null을 반환한다`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)

        // when
        val expectedExample = exampleRepositoryAdapter.getById(0)

        // then
        assertNull(expectedExample)
    }

    @Test
    fun `모든 Example 조회 테스트`() {
        // given
        val exampleEntityList = mutableListOf(
            ExampleEntity(title = "title", description = "description"),
            ExampleEntity(title = "similarTitle", description = "similarDescription"),
            ExampleEntity(title = "unexpectedTitle", description = "unexpectedDescription")
        )
        exampleJpaRepository.saveAll(exampleEntityList)

        // when
        val expectedExampleList = exampleRepositoryAdapter.getAll()

        // then
        assertNotNull(expectedExampleList)
        assertEquals(exampleEntityList.size, expectedExampleList.size)
    }

    @Test
    fun `모든 Example 조회 테스트 - 데이터가 존재하지 않을 경우`() {
        // when
        val expectedExampleList = exampleRepositoryAdapter.getAll()

        // then
        assertNotNull(expectedExampleList)
        assertTrue(expectedExampleList.isEmpty())
    }

    @Test
    @Transactional
    fun `Example 수정 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleId = exampleEntity.id!!
        val inputExample = Example(title = "modifiedTitle", description = "modifiedDescription")

        // when
        val expectedExample = exampleRepositoryAdapter.modify(exampleId = exampleId, inputExample)

        // then
        assertNotNull(expectedExample)
        assertEquals(inputExample.title, expectedExample.title)
        assertEquals(inputExample.description, expectedExample.description)
    }

    @Test
    fun `Example 수정 테스트 - 데이터가 존재하지 않을 경우 ExampleSearchException이 발생한다`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val unExpectedExampleId = 999L
        val inputExample = Example(title = "modifiedTitle", description = "modifiedDescription")

        // when & then
        val exception = assertThrows<ExampleSearchException> {
            exampleRepositoryAdapter.modify(exampleId = unExpectedExampleId, inputExample)
        }

        assertEquals("수정할 데이터를 찾을 수 없습니다.", exception.message)
    }

    @Test
    fun `Example 수정 테스트 - title이 null일 경우 IllegalArgumentException이 발생한다`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleId = exampleEntity.id!!
        val inputExample = Example(title = null, description = "modifiedDescription")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            exampleRepositoryAdapter.modify(exampleId = exampleId, inputExample)
        }

        assertEquals("제목은 null이 될 수 없습니다.", exception.message)
    }

    @Test
    fun `Example 수정 테스트 - description이 null일 경우 IllegalArgumentException이 발생한다`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleId = exampleEntity.id!!
        val inputExample = Example(title = "modifiedTitle", description = null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            exampleRepositoryAdapter.modify(exampleId = exampleId, inputExample)
        }

        assertEquals("설명은 null이 될 수 없습니다.", exception.message)
    }

    @Test
    fun `Example 삭제 테스트`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val exampleId = exampleEntity.id!!

        // when
        exampleRepositoryAdapter.remove(exampleId)

        // then
        val expectedExample = exampleJpaRepository.findById(exampleId)
        assertFalse(expectedExample.isPresent)
    }

    @Test
    fun `Example 삭제 테스트 - 데이터가 존재하지 않을 경우`() {
        // given
        val exampleEntity = ExampleEntity(title = "title", description = "description")
        exampleJpaRepository.save(exampleEntity)
        val unExpectedExampleId = 999L

        // when & then
        val exception = assertThrows<ExampleSearchException> {
            exampleRepositoryAdapter.remove(unExpectedExampleId)
        }
        assertEquals("삭제할 데이터를 찾을 수 없습니다.", exception.message)
    }
}