package com.example.translingo.presentation.favorite

import com.example.translingo.domain.model.Translation

data class FavoriteUiState(
    val searchQuery: String,
    val isEmpty: Boolean,
    val items: List<Translation>
)