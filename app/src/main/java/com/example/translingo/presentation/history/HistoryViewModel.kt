package com.example.translingo.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translingo.domain.model.History
import com.example.translingo.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> =
        languageRepository.getTranslationHistoryByDateDescAsFlow()
            .map {
                val groupedHistoryItems = produceGroupedHistoryByFormattedDate(it)
                HistoryUiState(groupedHistoryItems)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000L),
                HistoryUiState(emptyList())
            )


    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnDeleteHistory -> deleteHistory(event.history)
            is HistoryEvent.OnFavorite -> TODO()
        }
    }

    private fun deleteHistory(history: History) {
        viewModelScope.launch {
            languageRepository.deleteTranslation(history)
        }
    }


    private fun produceGroupedHistoryByFormattedDate(historyItems: List<History>): List<HistoryItem> {
        return historyItems.groupBy(History::date).mapKeys { entry ->
            parseLocalDateToDisplayFormat(entry.key)
        }.flatMap { (key, values) ->
            val headerList = listOf(HistoryItem.Header(key))
            val itemList = values.map(HistoryItem::Item)
            headerList + itemList
        }
    }


    private fun parseLocalDateToDisplayFormat(dateAsString: String): String {
        val localDate = LocalDate.parse(dateAsString)
        val today = LocalDate.now()
        return when {
            today.isEqual(localDate) -> "Today"
            today.minusDays(1).isEqual(localDate) -> "Yesterday"
            else -> {
                val displayMonth =
                    localDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                val dayOfMonth = localDate.dayOfMonth
                val year = localDate.year

                "$displayMonth $dayOfMonth, $year"
            }
        }
    }
}