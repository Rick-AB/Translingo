package com.example.translingo.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translingo.domain.model.Translation
import com.example.translingo.domain.repository.TranslationRepository
import com.example.translingo.util.Empty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val translationRepository: TranslationRepository
) : ViewModel() {

    private val searchQueryAsFlow = MutableStateFlow("")
    val uiState =
        searchQueryAsFlow.combine(translationRepository.getFavoriteTranslationsByDateDescAsFlow())
        { searchQuery, favoriteTranslations ->
            val shouldFilter = searchQuery.isNotEmpty() && searchQuery.isNotBlank()
            val filteredTranslations =
                if (shouldFilter) getFilteredList(searchQuery, favoriteTranslations)
                else favoriteTranslations

            val isEmpty = !shouldFilter && filteredTranslations.isEmpty()

            FavoriteUiState(
                searchQuery = searchQuery,
                isEmpty = isEmpty,
                items = filteredTranslations
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            FavoriteUiState(searchQuery = String.Empty, isEmpty = false, items = emptyList())
        )

    fun onEvent(event: FavoriteEvent) {
        when (event) {
            is FavoriteEvent.OnToggleFavorite -> toggleFavorite(event.translationId)
            is FavoriteEvent.OnSearchQueryChange -> searchQueryAsFlow.update { event.searchQuery }
        }
    }

    private fun toggleFavorite(translationId: Int) {
        viewModelScope.launch { translationRepository.toggleFavorite(translationId) }
    }

    private fun getFilteredList(
        searchQuery: String,
        favoriteTranslations: List<Translation>
    ): List<Translation> {
        return favoriteTranslations.filter {
            it.originalText.contains(searchQuery, ignoreCase = true) ||
                    it.translatedText.contains(searchQuery, ignoreCase = true)
        }
    }
}