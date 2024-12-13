package com.dicoding.retinova.ui.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.dicoding.retinova.R
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.api.ApiConfig
import com.dicoding.retinova.data.pref.UserPreference
import com.dicoding.retinova.databinding.ActivityHomeBinding
import com.dicoding.retinova.ui.deteksi.DeteksiFragment
import com.dicoding.retinova.ui.home.HomeFragment
import com.dicoding.retinova.ui.profil.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userRepository: UserRepository

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfig.getApiService()
        )

        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_detect -> replaceFragment(DeteksiFragment())
                R.id.navigation_person -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}