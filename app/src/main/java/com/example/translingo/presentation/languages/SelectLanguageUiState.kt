package com.example.translingo.presentation.languages

import com.example.translingo.domain.model.DownloadableLanguage
import com.example.translingo.domain.model.Language

data class SelectLanguageUiState(
    val languages: List<DownloadableLanguage>,
    val savedSourceLanguage: Language?,
    val savedTargetLanguage: Language?,
    val loading: Boolean
)