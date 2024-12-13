package com.dicoding.retinova.ui.catatan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_sugar_readings")
data class BloodSugarReading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val level: Double,
    val timestamp: Long,
    val notes: String? = null
)