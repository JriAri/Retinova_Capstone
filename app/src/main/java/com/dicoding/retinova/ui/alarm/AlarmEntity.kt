package com.dicoding.retinova.ui.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineName: String,
    val hour: Int,
    val minute: Int,
    val mealTiming: String
)
