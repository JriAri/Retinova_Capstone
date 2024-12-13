package com.dicoding.retinova.ui.deteksi

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeteksiViewModel : ViewModel() {
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> get() = _selectedImageUri

    fun setSelectedImageUri(uri: Uri?) {
        Log.d("DeteksiViewModel", "Image URI set: $uri")
        _selectedImageUri.value = null
        _selectedImageUri.value = uri
    }
}
