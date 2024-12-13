package com.dicoding.retinova.ui.catatan

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.retinova.databinding.ActivityAddBloodSugarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBloodSugarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBloodSugarBinding
    private val viewModel: AddBloodSugarViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBloodSugarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        observeViewModel()
        setupSaveButton()
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time))
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val bloodSugarLevel = binding.etBloodSugarLevel.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            when {
                bloodSugarLevel.isEmpty() -> {
                    binding.tilBloodSugarLevel.error = "Level gula darah tidak boleh kosong"
                    return@setOnClickListener
                }
                bloodSugarLevel.toDoubleOrNull() == null -> {
                    binding.tilBloodSugarLevel.error = "Masukkan level gula darah yang valid"
                    return@setOnClickListener
                }
                bloodSugarLevel.toDouble() < 0 -> {
                    binding.tilBloodSugarLevel.error = "Level gula darah tidak boleh negatif"
                    return@setOnClickListener
                }
                else -> {
                    binding.tilBloodSugarLevel.error = null
                    viewModel.saveBloodSugarReading(
                        level = bloodSugarLevel.toDouble(),
                        timestamp = calendar.timeInMillis,
                        notes = notes.ifEmpty { null }
                    )
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isDataSaved.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Pembacaan gula darah berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan pembacaan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}