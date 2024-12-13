package com.dicoding.retinova.ui.profil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.retinova.R
import com.dicoding.retinova.ui.view.MainActivity

class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)


        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            navigateBackToProfile()
        }
    }

    private fun navigateBackToProfile() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigate_to", "ProfileFragment")
        startActivity(intent)
        finish()
    }
}