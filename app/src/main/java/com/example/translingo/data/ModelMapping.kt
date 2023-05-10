package com.example.translingo.data

import com.example.translingo.data.database.entities.HistoryEntity
import com.example.translingo.domain.model.History
import com.example.translingo.util.toLanguage

fun History.toEntity() = HistoryEntity(
    id = id,
    originalText = originalText,
    translatedText = translatedText,
    sourceLanguageCode = sourceLanguage?.languageCode!!,
    targetLanguageCode = targetLanguage?.languageCode!!,
    isFavorite = isFavorite,
    date = date
)

fun HistoryEntity.toDomain(): History {
    return History(
        id,
        originalText,
        translatedText,
        sourceLanguageCode.toLanguage(),
        targetLanguageCode.toLanguage(),
        isFavorite,
        date
    )
}