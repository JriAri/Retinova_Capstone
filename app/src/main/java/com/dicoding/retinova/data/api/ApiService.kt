package com.dicoding.retinova.data.api

import com.dicoding.retinova.data.pref.User
import com.dicoding.retinova.data.response.LoginRequest
import com.dicoding.retinova.data.response.LoginResponse
import com.dicoding.retinova.data.response.RegisterRequest
import com.dicoding.retinova.data.response.RegisterResponse
import retrofit2.http.*

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}