package com.dicoding.retinova.ui.alarm

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.retinova.R
import com.dicoding.retinova.data.di.Injection
import com.dicoding.retinova.databinding.ActivityAlarmBinding
import com.dicoding.retinova.ui.ViewModelFactory

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarmViewModel: AlarmViewModel by viewModels {
        val userRepository = Injection.provideRepository(this)
        val alarmDao = Injection.provideAlarmDao(this)
        ViewModelFactory(userRepository, alarmDao)
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermission()

        setupRecyclerView()
        setupToolbar()
        observeViewModel()

        binding.btnAddReminder.setOnClickListener {
            val medicineName = binding.etMedicineName.text.toString().trim()
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute
            val mealTiming = when (binding.rgMealTiming.checkedRadioButtonId) {
                R.id.rbBeforeMeal -> "Sebelum Makan"
                R.id.rbDuringMeal -> "Saat Makan"
                R.id.rbAfterMeal -> "Sesudah Makan"
                else -> null
            }

            if (medicineName.isNotEmpty() && mealTiming != null) {
                alarmViewModel.setAlarm(this, medicineName, hour, minute, mealTiming)
                binding.etMedicineName.text.clear()
            } else {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmAdapter { alarm ->
            AlertDialog.Builder(this)
                .setTitle("Hapus Alarm")
                .setMessage("Apakah Anda yakin ingin menghapus alarm ini?")
                .setPositiveButton("Ya") { _, _ ->
                    alarmViewModel.removeAlarm(alarm, this)
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        binding.rvAlarmList.apply {
            layoutManager = LinearLayoutManager(this@AlarmActivity)
            adapter = alarmAdapter
        }
    }

    private fun observeViewModel() {
        alarmViewModel.alarmState.observe(this) { state ->
            when (state) {
                is AlarmState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.etMedicineName.text.clear()
                }
                is AlarmState.Error -> {
                    Toast.makeText(this, state.error, Toast.LENGTH_SHORT).show()
                }
                is AlarmState.Loading -> {
                }
            }
        }

        alarmViewModel.alarms.observe(this) { alarms ->
            alarmAdapter.submitList(alarms)

            binding.emptyStateLayout.visibility = if (alarms.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAlarmList.visibility = if (alarms.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION
                )
            } else {
                Toast.makeText(this, "Izin notifikasi sudah diberikan", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
