package com.example.translingo.presentation.history

import com.example.translingo.domain.model.Translation

sealed interface HistoryEvent {
    data class OnDeleteHistory(val translation: Translation) : HistoryEvent
    data class OnToggleFavorite(val historyId: Int) : HistoryEvent
}