package com.dicoding.retinova.ui.alarm

sealed class AlarmState {
    object Loading : AlarmState()
    data class Success(val message: String) : AlarmState()
    data class Error(val error: String) : AlarmState()
}
