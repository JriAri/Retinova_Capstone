package com.dicoding.retinova.data.pref

data class User(
    val id: String = "",
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)