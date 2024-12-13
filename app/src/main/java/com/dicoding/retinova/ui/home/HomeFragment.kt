package com.dicoding.retinova.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.api.ApiConfig
import com.dicoding.retinova.data.pref.UserPreference
import com.dicoding.retinova.databinding.FragmentHomeBinding
import com.dicoding.retinova.ui.ViewModelFactory
import com.dicoding.retinova.ui.alarm.AlarmActivity
import com.dicoding.retinova.ui.catatan.CatatanActivity

val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(
            UserRepository.getInstance(
                UserPreference.getInstance(requireContext().userPreferencesDataStore),
                ApiConfig.getApiService()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.welcomeText.observe(viewLifecycleOwner) { text ->
            binding.welcomeText.text = text
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            val healthNotesCard = featureIcons.getChildAt(0)
            val medicineReminderCard = featureIcons.getChildAt(1)

            healthNotesCard.setOnClickListener {
                val intent = Intent(requireContext(), CatatanActivity::class.java)
                startActivity(intent)
            }

            medicineReminderCard.setOnClickListener {
                val intent = Intent(requireContext(), AlarmActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}