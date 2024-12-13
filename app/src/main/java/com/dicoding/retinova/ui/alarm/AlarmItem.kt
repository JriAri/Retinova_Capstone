package com.dicoding.retinova.ui.alarm

data class AlarmItem(
    val id: Long,
    val medicineName: String,
    val hour: Int,
    val minute: Int,
    val mealTiming: String
)
