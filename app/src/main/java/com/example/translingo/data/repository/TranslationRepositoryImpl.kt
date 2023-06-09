package com.example.translingo.data.repository

import com.example.translingo.data.database.TranslingoDatabase
import com.example.translingo.data.database.entities.TranslationEntity
import com.example.translingo.data.toDomain
import com.example.translingo.data.toEntity
import com.example.translingo.domain.model.Translation
import com.example.translingo.domain.repository.TranslationRepository
import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    database: TranslingoDatabase
) : TranslationRepository {
    private val unidentifiableLanguageCode = "und"
    private val historyDao = database.historyDao()
    override suspend fun saveTranslation(translation: Translation) {
        if (!validateTranslation(translation.translatedText)) return
        historyDao.insertTranslation(translation.toEntity())
    }

    override suspend fun deleteTranslation(translation: Translation) {
        historyDao.deleteTranslation(translation.toEntity())
    }

    override fun getTranslationHistoryByDateDescAsFlow(): Flow<List<Translation>> {
        return historyDao.getTranslationsByDateDesc()
            .map { list -> list.map(TranslationEntity::toDomain) }
    }

    override suspend fun toggleFavorite(id: Int) {
        historyDao.toggleFavorite(id)
    }

    override fun getFavoriteTranslationsByDateDescAsFlow(): Flow<List<Translation>> {
        return historyDao.getFavoriteTranslationsByDateDesc()
            .map { list -> list.map(TranslationEntity::toDomain) }
    }

    private suspend fun validateTranslation(translatedText: String): Boolean {
        val languageIdentifier = LanguageIdentification.getClient()
        val identifiedLanguage = languageIdentifier.identifyLanguage(translatedText).await()
        return identifiedLanguage != unidentifiableLanguageCode
    }
}