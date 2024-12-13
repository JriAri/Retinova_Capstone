package com.dicoding.retinova.ui.catatan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CatatanViewModel(application: Application) : AndroidViewModel(application) {
    private val database = RetirovaDatabase.getDatabase(application)
    private val bloodSugarDao = database.bloodSugarDao()

    private val allBloodSugarReadings = bloodSugarDao.getAllReadings()

    val averageBloodSugarLevel: LiveData<Double> = allBloodSugarReadings.switchMap { readings ->
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgo = calendar.timeInMillis

        val filteredReadings = readings.filter { it.timestamp >= sevenDaysAgo }

        val avgLevel = if (filteredReadings.isNotEmpty()) {
            filteredReadings.map { it.level }.average()
        } else {
            0.0
        }

        MutableLiveData(avgLevel)
    }

    private val _bloodSugarLevel = MutableLiveData<Double>()
    val bloodSugarLevel: LiveData<Double> = _bloodSugarLevel

    private val _progressPercentage = MutableLiveData<Float>()
    val progressPercentage: LiveData<Float> = _progressPercentage

    val bloodSugarChartData: LiveData<List<Pair<String, Float>>> = allBloodSugarReadings.switchMap { readings ->
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgo = calendar.timeInMillis

        val filteredReadings = readings.filter { it.timestamp >= sevenDaysAgo }

        val formatter = SimpleDateFormat("dd-MM", Locale.getDefault())
        val grouped = filteredReadings.groupBy { reading ->
            val date = Date(reading.timestamp)
            formatter.format(date)
        }

        val chartData = grouped.map { (date, dailyReadings) ->
            val average = dailyReadings.map { it.level }.average().toFloat()
            date to average
        }.sortedBy { it.first }

        MutableLiveData(chartData)
    }

    private fun updateBloodSugarLevel(level: Double) {
        _bloodSugarLevel.value = level

        val maxLevel = 200.0
        val progress = (level / maxLevel * 100).toFloat().coerceIn(0f, 100f)
        _progressPercentage.value = progress
    }

    fun addBloodSugarReading(level: Double, notes: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val reading = BloodSugarReading(
                level = level,
                timestamp = System.currentTimeMillis(),
                notes = notes
            )
            bloodSugarDao.insert(reading)

            updateBloodSugarLevel(level)
        }
    }
}
