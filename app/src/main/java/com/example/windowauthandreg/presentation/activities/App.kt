package com.example.windowauthandreg.presentation.activities

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

// App.kt
@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}