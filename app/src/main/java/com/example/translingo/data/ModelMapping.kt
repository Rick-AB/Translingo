package com.example.translingo.data

import com.example.translingo.data.database.entities.TranslationEntity
import com.example.translingo.domain.model.Translation
import com.example.translingo.util.toLanguage

fun Translation.toEntity() = TranslationEntity(
    id = id,
    originalText = originalText,
    translatedText = translatedText,
    sourceLanguageCode = sourceLanguage?.languageCode!!,
    targetLanguageCode = targetLanguage?.languageCode!!,
    isFavorite = isFavorite,
    date = date
)

fun TranslationEntity.toDomain(): Translation {
    return Translation(
        id,
        originalText,
        translatedText,
        sourceLanguageCode.toLanguage(),
        targetLanguageCode.toLanguage(),
        isFavorite,
        date
    )
}