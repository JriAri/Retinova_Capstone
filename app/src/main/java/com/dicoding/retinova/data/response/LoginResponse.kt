package com.dicoding.retinova.data.response

data class LoginResponse(
    val message: String,
    val token: String? = null,
    val userId: String? = null,
    val error: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)