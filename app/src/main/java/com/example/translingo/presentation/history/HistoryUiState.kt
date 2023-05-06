package com.example.translingo.presentation.history

import com.example.translingo.domain.model.History

data class HistoryUiState(
    val items: List<HistoryItem>
)

sealed interface HistoryItem {
    data class Item(val history: History) : HistoryItem
    data class Header(val text: String) : HistoryItem
}
