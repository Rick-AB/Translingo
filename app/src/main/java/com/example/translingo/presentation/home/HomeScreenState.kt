package com.example.translingo.presentation.home

import com.example.translingo.domain.model.Language
import com.example.translingo.util.Empty

data class HomeScreenState(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: Language,
    val targetLanguage: Language
) {
    companion object {
        fun default(): HomeScreenState {
            val emptyLanguage = Language(String.Empty, String.Empty)
            return HomeScreenState(
                String.Empty,
                String.Empty,
                emptyLanguage,
                emptyLanguage
            )
        }
    }
}
