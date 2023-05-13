package com.example.translingo.presentation.history

import com.example.translingo.domain.model.Translation

data class HistoryUiState(
    val items: List<HistoryItem>
)

sealed interface HistoryItem {
    data class Item(val translation: Translation) : HistoryItem
    data class Header(val text: String) : HistoryItem
}
