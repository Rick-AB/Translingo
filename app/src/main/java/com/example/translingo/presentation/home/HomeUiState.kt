package com.example.translingo.presentation.home

import com.example.translingo.domain.model.Language
import com.example.translingo.util.Empty

data class HomeUiState(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val loading: Boolean
) {
    companion object {
        fun default(): HomeUiState {
            val emptyLanguage = Language(String.Empty, String.Empty)
            return HomeUiState(
                String.Empty,
                String.Empty,
                emptyLanguage,
                emptyLanguage,
                loading = false
            )
        }
    }
}
