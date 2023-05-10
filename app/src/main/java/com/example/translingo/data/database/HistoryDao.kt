package com.example.translingo.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.translingo.data.database.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity ORDER BY date DESC")
    fun getHistoryByDateDesc(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM HistoryEntity WHERE id=:id")
    suspend fun getHistoryById(id: Int): HistoryEntity?
}