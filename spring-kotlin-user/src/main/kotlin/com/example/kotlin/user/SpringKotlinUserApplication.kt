package com.example.kotlin.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKotlinUserApplication

fun main(args: Array<String>) {
	runApplication<SpringKotlinUserApplication>(*args)
}
