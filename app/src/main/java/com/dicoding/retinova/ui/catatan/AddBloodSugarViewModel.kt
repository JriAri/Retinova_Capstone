package com.dicoding.retinova.ui.catatan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddBloodSugarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BloodSugarRepository

    private val _isDataSaved = MutableLiveData<Boolean>()
    val isDataSaved: LiveData<Boolean> = _isDataSaved

    init {
        val bloodSugarDao = RetirovaDatabase.getDatabase(application).bloodSugarDao()
        repository = BloodSugarRepository(bloodSugarDao)
    }

    fun saveBloodSugarReading(level: Double, timestamp: Long, notes: String? = null) {
        viewModelScope.launch {
            try {
                require(level >= 0) { "Blood sugar level cannot be negative" }

                val reading = BloodSugarReading(
                    level = level,
                    timestamp = timestamp,
                    notes = notes
                )
                repository.insert(reading)
                _isDataSaved.value = true
            } catch (e: Exception) {
                _isDataSaved.value = false
            }
        }
    }
}