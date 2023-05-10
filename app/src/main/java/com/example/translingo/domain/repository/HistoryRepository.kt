package com.example.translingo.domain.repository

import com.example.translingo.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    suspend fun saveTranslation(history: History)

    suspend fun deleteTranslation(history: History)

    fun getTranslationHistoryByDateDescAsFlow(): Flow<List<History>>

    suspend fun addOrRemoveFavorite(id: Int)
}