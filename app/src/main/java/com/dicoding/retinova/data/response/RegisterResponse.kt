package com.dicoding.retinova.data.response

data class RegisterResponse(
    val message: String,
    val userId: String? = null,
    val error: String? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
