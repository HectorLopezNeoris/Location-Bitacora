package com.example.locationbitacora

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun location(): LocationDao
}