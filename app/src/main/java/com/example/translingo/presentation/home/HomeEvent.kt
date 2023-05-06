package com.example.translingo.presentation.home

sealed class HomeEvent {
    object OnForeground : HomeEvent()
    data class OnTranslate(val text: String) : HomeEvent()
    data class OnSwapLanguages(
        val newSourceLanguageCode: String,
        val newTargetLanguageCode: String
    ) : HomeEvent()
}
