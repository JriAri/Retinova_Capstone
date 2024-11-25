package com.dicoding.retinova.data

import com.dicoding.retinova.data.pref.User
import com.dicoding.retinova.data.pref.UserPreference
import com.dicoding.retinova.data.response.LoginRequest
import com.dicoding.retinova.data.response.LoginResponse
import com.dicoding.retinova.data.api.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: User) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<User> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(email, password))
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}