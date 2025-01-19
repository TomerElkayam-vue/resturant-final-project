package com.example.finalprojectresturant

import android.app.Application
import com.example.finalprojectresturant.room.DatabaseHolder

class ApplicationStarter: Application() {
    override fun onCreate() {
        DatabaseHolder.initDatabase(this)
        super.onCreate()
    }
}