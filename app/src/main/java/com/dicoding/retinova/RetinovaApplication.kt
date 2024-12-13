package com.dicoding.retinova

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize

class RetinovaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}