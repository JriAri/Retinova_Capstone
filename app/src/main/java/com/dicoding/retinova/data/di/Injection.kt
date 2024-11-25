package com.dicoding.retinova.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.api.ApiConfig
import com.dicoding.retinova.data.pref.UserPreference
import com.dicoding.retinova.ui.alarm.AlarmDao
import com.dicoding.retinova.ui.alarm.AlarmDatabase

val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val dataStore = context.userPreferencesDataStore
        val pref = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideAlarmDao(context: Context): AlarmDao {
        return AlarmDatabase.getDatabase(context).alarmDao()
    }

}
