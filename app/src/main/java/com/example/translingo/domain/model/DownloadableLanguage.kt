package com.example.translingo.domain.model

data class DownloadableLanguage(
    val language: Language,
    val isDownloaded: Boolean,
    val isDownloading: Boolean
)
