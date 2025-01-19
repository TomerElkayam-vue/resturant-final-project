package com.example.finalprojectresturant.room

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseHolder {
    private var appDatabase: AppDatabase? = null

    fun initDatabase(context: Context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "just-eat-db").fallbackToDestructiveMigration()
            .build()
    }

    fun getDatabase(): AppDatabase {
        return this.appDatabase!!
    }
}