package com.example.kotlin.presentation.api

import com.example.kotlin.business.ExampleService
import com.example.kotlin.business.domain.Example
import com.example.kotlin.dto.ExampleCreateRequest
import com.example.kotlin.dto.ExampleSearch
import com.example.kotlin.dto.ExampleUpdateRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/example")
class ExampleAPI(private val exampleService: ExampleService) {

    @PostMapping()
    fun save(@RequestBody exampleCreateRequest: ExampleCreateRequest): ResponseEntity<Example> {
        val example = Example(title = exampleCreateRequest.title, description = exampleCreateRequest.description)
        val createdExample = exampleService.save(example)
        return ResponseEntity.ok(createdExample)
    }

    @GetMapping("/{exampleId}")
    fun getExampleById(@PathVariable exampleId: Long): ResponseEntity<Example> {
        val example = exampleService.getById(exampleId)
        return ResponseEntity.ok(example ?: throw NoSuchElementException("${exampleId}에 해당하는 Example 이 존재하지 않습니다."))
    }

    @PutMapping()
    fun modify(@RequestBody exampleUpdateRequest: ExampleUpdateRequest): ResponseEntity<Example> {
        val example = Example(id=exampleUpdateRequest.id,title = exampleUpdateRequest.title, description = exampleUpdateRequest.description)
        val modifiedExample = exampleService.modify(example)
        return ResponseEntity.ok(modifiedExample)
    }

    @DeleteMapping("/{exampleId}")
    fun remove(@PathVariable exampleId: Long): ResponseEntity<Unit> {
        exampleService.remove(exampleId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    fun getByCriteria(exampleSearch: ExampleSearch, pageable: Pageable): ResponseEntity<List<Example>> {
        val examples = exampleService.getByCriteria(exampleSearch, pageable)
        return ResponseEntity.ok(examples)
    }
}