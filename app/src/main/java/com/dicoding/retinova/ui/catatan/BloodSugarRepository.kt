package com.dicoding.retinova.ui.catatan

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BloodSugarRepository(private val bloodSugarDao: BloodSugarDao) {
    val allReadings: LiveData<List<BloodSugarReading>> = bloodSugarDao.getAllReadings()

    suspend fun insert(reading: BloodSugarReading) {
        withContext(Dispatchers.IO) {
            bloodSugarDao.insert(reading)
        }
    }

    fun getReadingsBetweenDates(startDate: Long, endDate: Long): LiveData<List<BloodSugarReading>> {
        return bloodSugarDao.getReadingsBetweenDates(startDate, endDate)
    }
}