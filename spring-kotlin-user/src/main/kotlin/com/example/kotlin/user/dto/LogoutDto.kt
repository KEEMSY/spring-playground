package com.example.kotlin.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class LogoutDto(
    @field:NotBlank
    @JsonProperty("token")
    private val _token: String?
) {
    val token: String
        get() = _token!!
}
