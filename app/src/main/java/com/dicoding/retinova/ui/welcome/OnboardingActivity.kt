package com.dicoding.retinova.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.retinova.R
import com.dicoding.retinova.data.UserRepository
import com.dicoding.retinova.data.api.ApiConfig
import com.dicoding.retinova.data.pref.UserPreference
import com.dicoding.retinova.databinding.ActivityOnboardingBinding
import com.dicoding.retinova.ui.login.LoginActivity
import com.dicoding.retinova.ui.view.HomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var userRepository: UserRepository
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "OnboardingActivity started successfully")

        userRepository = UserRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfig.getApiService()
        )
        checkUserSession()

        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.deteksi_diabetes,
                getString(R.string.onboarding_title_detection),
                getString(R.string.onboarding_desc_detection)
            ),
            OnboardingItem(
                R.drawable.chatbot,
                getString(R.string.onboarding_title_chatbot),
                getString(R.string.onboarding_desc_chatbot)
            ),
            OnboardingItem(
                R.drawable.artikel,
                getString(R.string.onboarding_title_article),
                getString(R.string.onboarding_desc_article)
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)

        updateBottomSheetContent(onboardingItems[0])

        setupButtonListeners(onboardingItems)

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateBottomSheetContent(onboardingItems[position])
                binding.btnNext.text = if (position == onboardingItems.size - 1)
                    getString(R.string.get_started) else getString(R.string.next)
                binding.btnBack.visibility = if (position > 0) View.VISIBLE else View.GONE
                Log.d(TAG, "ViewPager2 current page: $position")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
        Log.d(TAG, "OnboardingActivity destroyed")
    }

    private fun checkUserSession() {
        lifecycleScope.launch {
            try {
                val userSession = userRepository.getSession().first()
                Log.d(TAG, "User session retrieved: $userSession")
                if (userSession.isLogin) {
                    navigateToHomeActivity()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking user session", e)
            }
        }
    }

    private fun navigateToLoginActivity() {
        if (!isFinishing) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Log.d(TAG, "Navigated to com.dicoding.retinova.ui.login.LoginActivity")
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
        Log.d(TAG, "Navigated to HomeActivity")
    }

    private fun updateBottomSheetContent(item: OnboardingItem) {
        binding.textTitle.text = item.title
        binding.textDescription.text = item.description
        Log.d(TAG, "Updated bottom sheet content: ${item.title}")
    }

    private fun setupButtonListeners(onboardingItems: List<OnboardingItem>) {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < onboardingItems.size) {
                binding.viewPager.currentItem += 1
                Log.d(TAG, "Next button clicked, moved to page ${binding.viewPager.currentItem}")
            } else {
                navigateToLoginActivity()
            }
        }

        binding.btnSkip.setOnClickListener {
            navigateToLoginActivity()
            Log.d(TAG, "Skip button clicked")
        }

        binding.btnBack.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
                Log.d(TAG, "Back button clicked, moved to page ${binding.viewPager.currentItem}")
            }
        }
    }

    companion object {
        private const val TAG = "OnboardingActivity"
    }
}
