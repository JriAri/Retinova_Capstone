package com.dicoding.retinova.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.dicoding.retinova.data.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}