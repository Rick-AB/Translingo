package com.example.translingo.presentation.languages

sealed interface SelectLanguageEvent {
    data class OnSelectLanguage(
        val languageToSelectCode: String,
        val currentSelectedLanguageCode: String?,
        val otherLanguageCode: String?,
        val languageType: LanguageType
    ) : SelectLanguageEvent

    data class OnSearchQueryChange(val searchQuery: String) : SelectLanguageEvent
}