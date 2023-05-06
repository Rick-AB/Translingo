package com.example.translingo.data.repository

import com.example.translingo.data.UserPreference
import com.example.translingo.data.database.TranslingoDatabase
import com.example.translingo.data.database.entities.HistoryEntity
import com.example.translingo.data.toDomain
import com.example.translingo.data.toEntity
import com.example.translingo.domain.model.DownloadableLanguage
import com.example.translingo.domain.model.History
import com.example.translingo.domain.model.Language
import com.example.translingo.domain.repository.LanguageRepository
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val userPreference: UserPreference,
    database: TranslingoDatabase
) : LanguageRepository {
    private val historyDao = database.historyDao()
    override fun getDownloadableLanguages(): List<DownloadableLanguage> {
        return TranslateLanguage.getAllLanguages().map { languageCode ->
            val displayName = Locale(languageCode).displayName
            val language = Language(languageCode, displayName)
            DownloadableLanguage(language, isDownloaded = false, isDownloading = false)
        }
    }

    override fun getSourceLanguageAsFlow(): Flow<Language?> {
        return userPreference.getSourceLanguageAsFlow.map { languageCode ->
            getLanguageFromCode(languageCode)
        }
    }

    override suspend fun getSourceLanguage(): Language? {
        val languageCode = userPreference.getSourceLanguageAsFlow.first()
        return getLanguageFromCode(languageCode)
    }

    override fun getTargetLanguageAsFlow(): Flow<Language?> {
        return userPreference.getTargetLanguageAsFlow.map { languageCode ->
            getLanguageFromCode(languageCode)
        }
    }

    override suspend fun getTargetLanguage(): Language? {
        val languageCode = userPreference.getTargetLanguageAsFlow.first()
        return getLanguageFromCode(languageCode)
    }

    override suspend fun setSourceLanguage(languageCode: String) =
        userPreference.setSourceLanguage(languageCode)

    override suspend fun setTargetLanguage(languageCode: String) =
        userPreference.setTargetLanguage(languageCode)

    override suspend fun saveTranslation(history: History) {
        historyDao.insertHistory(history.toEntity())
    }

    override suspend fun deleteTranslation(history: History) {
        historyDao.deleteHistory(history.toEntity())
    }

    override fun getTranslationHistoryByDateDescAsFlow(): Flow<List<History>> {
        return historyDao.getHistoryByDateDesc().map { list -> list.map(HistoryEntity::toDomain) }
    }

    private fun getLanguageFromCode(languageCode: String): Language? {
        val locale = Locale(languageCode)
        val isValidLocale = Locale.getAvailableLocales().contains(locale)
        return if (isValidLocale) Language(languageCode, locale.displayName)
        else null
    }
}