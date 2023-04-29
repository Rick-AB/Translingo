package com.example.translingo.presentation.navigation

import com.example.translingo.presentation.languages.LanguageType
import com.kiwi.navigationcompose.typed.Destination
import kotlinx.serialization.Serializable

sealed interface Destinations : Destination {
    @Serializable
    object Home : Destinations

    @Serializable
    data class SelectLanguage(val languageType: LanguageType) : Destinations
}