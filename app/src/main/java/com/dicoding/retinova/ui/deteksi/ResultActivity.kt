package com.dicoding.retinova.ui.deteksi

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.retinova.databinding.ActivityResultBinding
import android.widget.Toast

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                binding.imageView.setImageBitmap(bitmap)

                ApiClassifierHelper.classifyImage(this, bitmap) { result ->
                    runOnUiThread {
                        binding.resultTextView.text = result
                    }
                }
            } catch (e: Exception) {
                Log.e("ResultActivity", "Error processing image", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.resultTextView.text = "Gagal memproses gambar"
            }
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}