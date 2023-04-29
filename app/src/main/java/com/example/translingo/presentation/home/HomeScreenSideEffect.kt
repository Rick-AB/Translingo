package com.example.translingo.presentation.home

import com.example.translingo.presentation.languages.LanguageType

sealed interface HomeScreenSideEffect {
    data class SelectLanguage(val languageType: LanguageType) : HomeScreenSideEffect
}