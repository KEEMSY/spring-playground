package com.example.springkotlinmvc.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/example")
class ExampleController {

    @GetMapping("/")
    fun getExampleList(): List<String> {
        return listOf("example1", "example2", "example3")
    }
}