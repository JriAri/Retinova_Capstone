package com.dicoding.retinova.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.retinova.data.UserRepository

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _welcomeText = MutableLiveData<String>()
    val welcomeText: LiveData<String> = _welcomeText

    init {
        _welcomeText.value = "Selamat Datang"
    }
}
