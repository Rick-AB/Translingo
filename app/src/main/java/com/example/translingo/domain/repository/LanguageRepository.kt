package com.example.translingo.domain.repository

import com.example.translingo.domain.model.DownloadableLanguage
import com.example.translingo.domain.model.Language
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {

    fun getDownloadableLanguages(): List<DownloadableLanguage>

    fun getSourceLanguageAsFlow(): Flow<Language?>

    suspend fun getSourceLanguage(): Language?

    fun getTargetLanguageAsFlow(): Flow<Language?>

    suspend fun getTargetLanguage(): Language?

    suspend fun setSourceLanguage(languageCode: String)

    suspend fun setTargetLanguage(languageCode: String)
}