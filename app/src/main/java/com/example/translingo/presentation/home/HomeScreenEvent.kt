package com.example.translingo.presentation.home

sealed class HomeScreenEvent {
    object OnForeground : HomeScreenEvent()
    data class OnTranslate(val text: String) : HomeScreenEvent()
    data class OnSwapLanguages(
        val newSourceLanguageCode: String,
        val newTargetLanguageCode: String
    ) : HomeScreenEvent()
}
