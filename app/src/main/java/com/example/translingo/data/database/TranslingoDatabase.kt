package com.example.translingo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.translingo.data.database.entities.TranslationEntity

@Database(entities = [TranslationEntity::class], version = 3)
abstract class TranslingoDatabase : RoomDatabase() {
    abstract fun historyDao(): TranslationDao
}