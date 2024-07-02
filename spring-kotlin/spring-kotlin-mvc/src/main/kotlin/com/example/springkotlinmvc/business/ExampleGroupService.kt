package com.example.springkotlinmvc.business

import com.example.springkotlinmvc.model.entity.ExampleEntity
import com.example.springkotlinmvc.model.entity.ExampleGroupOneToMany
import com.example.springkotlinmvc.model.repository.ExampleGroupOneToManyJpaRepository
import com.example.springkotlinmvc.util.fail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ExampleGroupService (
    private val exampleGroupOneToManyJpaRepository: ExampleGroupOneToManyJpaRepository
){
    @Transactional
    fun saveExampleGroup(name: String, examples: List<ExampleEntity>): ExampleGroupOneToMany {
        val exampleGroup = ExampleGroupOneToMany(name = name, examples = examples.toMutableList())
        examples.forEach { it.exampleGroup = exampleGroup } // 예시 그룹 설정
        return exampleGroupOneToManyJpaRepository.save(exampleGroup)
    }

    @Transactional(readOnly = true)
    fun getExampleGroupList(): List<ExampleGroupOneToMany> {
        val result = exampleGroupOneToManyJpaRepository.findAllWithExamples() ?: fail()
        return result.filterNotNull() // null 요소를 제거하여 반환 타입과 일치시키기
    }
}