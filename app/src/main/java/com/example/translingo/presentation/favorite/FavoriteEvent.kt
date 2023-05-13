package com.example.translingo.presentation.favorite

sealed interface FavoriteEvent {
    data class OnSearchQueryChange(val searchQuery: String) : FavoriteEvent
    data class OnToggleFavorite(val translationId: Int) : FavoriteEvent
}