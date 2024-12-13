package com.dicoding.retinova.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.api.ApiConfig
import com.dicoding.retinova.data.response.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    sealed class RegistrationState {
        data object Initial : RegistrationState()
        data object Registering : RegistrationState()
        data object RegistrationSuccess : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val apiService = ApiConfig.getApiService()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val validationResult = validateRegistration(name, email, password, confirmPassword)
        if (!validationResult.first) {
            _registrationState.value = RegistrationState.Error(validationResult.second)
            return
        }

        _registrationState.value = RegistrationState.Registering

        viewModelScope.launch {
            try {
                val registerRequest = RegisterRequest(name, email, password)

                val response = apiService.register(registerRequest)

                if (response.error != null) {
                    _registrationState.value = RegistrationState.Error(response.error)
                } else {
                    _registrationState.value = RegistrationState.RegistrationSuccess
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Registration failed: ${e.message}")
            }
        }
    }


    private fun validateRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Pair<Boolean, String> {
        if (name.isEmpty()) {
            return Pair(false, "Name cannot be empty")
        }

        if (email.isEmpty()) {
            return Pair(false, "Email cannot be empty")
        }

        if (password.isEmpty()) {
            return Pair(false, "Password cannot be empty")
        }

        if (confirmPassword.isEmpty()) {
            return Pair(false, "Confirm password cannot be empty")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Pair(false, "Invalid email format")
        }

        if (password.length < 8) {
            return Pair(false, "Password must be at least 8 characters")
        }

        if (password != confirmPassword) {
            return Pair(false, "Passwords do not match")
        }

        return Pair(true, "")
    }
}
