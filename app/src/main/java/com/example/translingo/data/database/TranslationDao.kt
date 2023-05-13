package com.example.translingo.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.translingo.data.database.entities.TranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity)

    @Delete
    suspend fun deleteTranslation(translation: TranslationEntity)

    @Query("SELECT * FROM TranslationEntity ORDER BY date DESC")
    fun getTranslationsByDateDesc(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM TranslationEntity WHERE isFavorite=1 ORDER BY date DESC")
    fun getFavoriteTranslationsByDateDesc(): Flow<List<TranslationEntity>>

    @Query("UPDATE TranslationEntity SET isFavorite = NOT isFavorite WHERE id=:id")
    suspend fun toggleFavorite(id: Int)
}