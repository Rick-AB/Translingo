package com.example.translingo.domain.model

data class History(
    val id: Int,
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: Language?,
    val targetLanguage: Language?,
    val isFavorite: Boolean,
    val date: String
)
