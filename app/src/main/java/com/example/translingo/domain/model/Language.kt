package com.example.translingo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val languageCode: String,
    val displayName: String
)
