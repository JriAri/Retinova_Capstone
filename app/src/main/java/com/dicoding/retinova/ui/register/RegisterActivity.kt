package com.dicoding.retinova.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.retinova.R
import com.dicoding.retinova.databinding.ActivityRegisterBinding
import com.dicoding.retinova.data.di.Injection
import com.dicoding.retinova.ui.ViewModelFactory
import com.dicoding.retinova.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()

        registerViewModel.registrationState.observe(this) { state ->
            handleRegistrationState(state)
        }
    }

    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            registerViewModel.register(name, email, password, confirmPassword)
        }

        binding.tvLoginNow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegistrationState(state: RegisterViewModel.RegistrationState) {
        when (state) {
            is RegisterViewModel.RegistrationState.Initial -> {
                binding.progressBar.visibility = View.GONE
                binding.mainRegistrationLayout.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = true
            }
            is RegisterViewModel.RegistrationState.Registering -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            }
            is RegisterViewModel.RegistrationState.RegistrationSuccess -> {
                Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            is RegisterViewModel.RegistrationState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
