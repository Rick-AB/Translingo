package com.example.translingo.presentation.languages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translingo.domain.model.DownloadableLanguage
import com.example.translingo.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val allLanguagesAsFlow = MutableStateFlow(languageRepository.getDownloadableLanguages())
    private val searchQueryAsFlow = MutableStateFlow("")
    private val loadingAsFlow = MutableStateFlow(false)

    val uiState = combine(
        searchQueryAsFlow,
        allLanguagesAsFlow,
        languageRepository.getSourceLanguageAsFlow(),
        languageRepository.getTargetLanguageAsFlow(),
        loadingAsFlow
    )
    { searchQuery, allLanguages, savedSourceLanguage, savedTargetLanguage, loading ->
        val shouldFilter = searchQuery.isNotEmpty() && searchQuery.isNotBlank()
        val filteredLanguages = if (shouldFilter) getFilteredList(allLanguages, searchQuery)
        else allLanguages

        SelectLanguageUiState(
            searchQuery = searchQuery,
            languages = filteredLanguages,
            savedSourceLanguage = savedSourceLanguage,
            savedTargetLanguage = savedTargetLanguage,
            loading = loading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), initialValue = null)

    private val sideEffectChannel = Channel<SelectLanguageSideEffect>()
    val sideEffect = sideEffectChannel.receiveAsFlow()

    fun onEvent(event: SelectLanguageEvent) {
        when (event) {
            is SelectLanguageEvent.OnSearchQueryChange -> {
                searchQueryAsFlow.value = event.searchQuery
            }

            is SelectLanguageEvent.OnSelectLanguage -> {
                selectLanguage(
                    languageToSelectCode = event.languageToSelectCode,
                    currentSelectedLanguageCode = event.currentSelectedLanguageCode,
                    otherLanguageCode = event.otherLanguageCode,
                    languageType = event.languageType
                )
            }
        }
    }

    private fun selectLanguage(
        languageToSelectCode: String,
        currentSelectedLanguageCode: String?,
        otherLanguageCode: String?,
        languageType: LanguageType
    ) {
        viewModelScope.launch {
            loadingAsFlow.value = true
            when (languageType) {
                LanguageType.SOURCE -> {
                    if (languageToSelectCode == otherLanguageCode) {
                        launch { languageRepository.setTargetLanguage(currentSelectedLanguageCode!!) }
                        launch { languageRepository.setSourceLanguage(languageToSelectCode) }
                    } else {
                        languageRepository.setSourceLanguage(languageToSelectCode)
                    }
                    handleLanguageSelected()
                }

                LanguageType.TARGET -> {
                    if (languageToSelectCode == otherLanguageCode) {
                        launch { languageRepository.setSourceLanguage(currentSelectedLanguageCode!!) }
                        launch { languageRepository.setTargetLanguage(languageToSelectCode) }
                    } else {
                        languageRepository.setTargetLanguage(languageToSelectCode)
                    }
                    handleLanguageSelected()
                }
            }
        }
    }

    private suspend fun handleLanguageSelected() {
        loadingAsFlow.value = false
        sideEffectChannel.send(SelectLanguageSideEffect.OnLanguageSelected)
    }

    private fun getFilteredList(
        allLanguages: List<DownloadableLanguage>,
        searchQuery: String
    ): List<DownloadableLanguage> {
        return allLanguages.filter { downloadableLanguage ->
            val name = downloadableLanguage.language.displayName
            val code = downloadableLanguage.language.languageCode
            name.contains(searchQuery, ignoreCase = true) ||
                    code.contains(searchQuery, ignoreCase = true)
        }
    }
}