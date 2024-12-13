package com.dicoding.retinova.ui.catatan

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface BloodSugarDao {
    @Insert
    suspend fun insert(reading: BloodSugarReading)

    @Query("SELECT * FROM blood_sugar_readings ORDER BY timestamp DESC")
    fun getAllReadings(): LiveData<List<BloodSugarReading>>

    @Query("SELECT * FROM blood_sugar_readings WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getReadingsBetweenDates(startDate: Long, endDate: Long): LiveData<List<BloodSugarReading>>


}