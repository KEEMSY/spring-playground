package com.example.kotlin.model.exception

class ExampleSearchException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}