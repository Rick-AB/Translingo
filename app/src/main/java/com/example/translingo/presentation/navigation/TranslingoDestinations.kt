package com.example.translingo.presentation.navigation

import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.languages.LanguageType
import com.kiwi.navigationcompose.typed.Destination
import kotlinx.serialization.Serializable

sealed interface TranslingoDestinations : Destination {
    @Serializable
    data class Home(val translation: Translation?) : TranslingoDestinations

    @Serializable
    data class SelectLanguage(val languageType: LanguageType) : TranslingoDestinations

    @Serializable
    object Favorite : TranslingoDestinations
}