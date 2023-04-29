package com.example.translingo.presentation.languages

sealed interface SelectLanguageSideEffect {
    object OnLanguageSelected : SelectLanguageSideEffect
}