package com.example.translingo.presentation.history

import com.example.translingo.domain.model.History

sealed interface HistoryEvent {
    data class OnDeleteHistory(val history: History) : HistoryEvent
    data class OnFavorite(val history: History) : HistoryEvent
}