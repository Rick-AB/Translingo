package com.example.translingo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    val id: Int,
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: Language?,
    val targetLanguage: Language?,
    val isFavorite: Boolean,
    val date: String
)
