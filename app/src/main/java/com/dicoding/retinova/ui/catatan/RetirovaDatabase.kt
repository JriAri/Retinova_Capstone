package com.dicoding.retinova.ui.catatan

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BloodSugarReading::class], version = 1, exportSchema = false)
abstract class RetirovaDatabase : RoomDatabase() {
    abstract fun bloodSugarDao(): BloodSugarDao

    companion object {
        @Volatile
        private var INSTANCE: RetirovaDatabase? = null

        fun getDatabase(context: Context): RetirovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RetirovaDatabase::class.java,
                    "retinova_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}