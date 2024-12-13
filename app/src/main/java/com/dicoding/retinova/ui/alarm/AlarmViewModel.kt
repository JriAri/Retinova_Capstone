package com.dicoding.retinova.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.dicoding.retinova.data.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmViewModel(
    private val repository: UserRepository,
    private val alarmDao: AlarmDao
) : ViewModel() {

    private val _alarmState = MutableLiveData<AlarmState>()
    val alarmState: LiveData<AlarmState> = _alarmState

    val alarms: LiveData<List<AlarmItem>> = alarmDao.getAllAlarms()
        .map { entities ->
            entities.map { entity ->
                AlarmItem(
                    id = entity.id,
                    medicineName = entity.medicineName,
                    hour = entity.hour,
                    minute = entity.minute,
                    mealTiming = entity.mealTiming
                )
            }
        }
        .asLiveData()

    fun setAlarm(context: Context, name: String, hour: Int, minute: Int, mealTiming: String) {
        _alarmState.value = AlarmState.Loading
        viewModelScope.launch {
            try {
                val newAlarmEntity = AlarmEntity(
                    medicineName = name,
                    hour = hour,
                    minute = minute,
                    mealTiming = mealTiming
                )

                alarmDao.insertAlarm(newAlarmEntity)

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("EXTRA_MEDICINE_NAME", name)
                    putExtra("EXTRA_MEAL_TIMING", mealTiming)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    name.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

                _alarmState.value = AlarmState.Success("Alarm berhasil ditambahkan")
            } catch (e: Exception) {
                _alarmState.value = AlarmState.Error("Terjadi kesalahan saat menambahkan alarm")
            }
        }
    }

    fun removeAlarm(alarm: AlarmItem, context: Context) {
        _alarmState.value = AlarmState.Loading
        viewModelScope.launch {
            try {
                val alarmEntity = AlarmEntity(
                    id = alarm.id,
                    medicineName = alarm.medicineName,
                    hour = alarm.hour,
                    minute = alarm.minute,
                    mealTiming = alarm.mealTiming
                )

                alarmDao.deleteAlarm(alarmEntity)

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()

                _alarmState.value = AlarmState.Success("Alarm berhasil dihapus")
            } catch (e: Exception) {
                _alarmState.value = AlarmState.Error("Terjadi kesalahan saat menghapus alarm")
            }
        }
    }

}