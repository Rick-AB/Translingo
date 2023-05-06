package com.example.translingo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.translingo.data.database.entities.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
abstract class TranslingoDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}