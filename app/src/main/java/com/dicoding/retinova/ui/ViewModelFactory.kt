package com.dicoding.retinova.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.ui.alarm.AlarmDao
import com.dicoding.retinova.ui.alarm.AlarmViewModel
import com.dicoding.retinova.ui.login.LoginViewModel
import com.dicoding.retinova.ui.register.RegisterViewModel
import com.dicoding.retinova.ui.home.HomeViewModel
import com.dicoding.retinova.ui.profil.ProfileViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val alarmDao: AlarmDao? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(userRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(userRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(userRepository) as T
            modelClass.isAssignableFrom(AlarmViewModel::class.java) -> {
                requireNotNull(alarmDao) { "AlarmDao is required for AlarmViewModel" }
                AlarmViewModel(userRepository, alarmDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
