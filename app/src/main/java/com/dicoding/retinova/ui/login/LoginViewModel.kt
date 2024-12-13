package com.dicoding.retinova.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.pref.User
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> = _loginError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)

                if (response.error != null) {
                    _loginError.value = response.message
                    _isLoggedIn.value = false
                } else {
                    val userModel = User(
                        email = email,
                        token = response.token ?: "",
                        isLogin = true
                    )
                    userRepository.saveSession(userModel)
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
                _isLoggedIn.value = false
            }
        }
    }

    fun saveGoogleSession(email: String, token: String) {
        viewModelScope.launch {
            val userModel = User(
                email = email,
                token = token,
                isLogin = true
            )
            userRepository.saveSession(userModel)
            _isLoggedIn.value = true
        }
    }

    fun getSession() = userRepository.getSession()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _isLoggedIn.value = false
        }
    }
}
