package com.example.translingo.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TranslationEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val originalText: String,
    val translatedText: String,
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val isFavorite: Boolean,
    val date: String
)
