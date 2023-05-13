package com.example.translingo.domain.repository

import com.example.translingo.domain.model.Translation
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun saveTranslation(translation: Translation)
    suspend fun deleteTranslation(translation: Translation)
    suspend fun toggleFavorite(id: Int)
    fun getTranslationHistoryByDateDescAsFlow(): Flow<List<Translation>>
    fun getFavoriteTranslationsByDateDescAsFlow(): Flow<List<Translation>>
}