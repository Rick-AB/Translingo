package com.example.translingo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userPreferences")
        private val SOURCE_LANGUAGE_KEY = stringPreferencesKey("source_language")
        private val TARGET_LANGUAGE_KEY = stringPreferencesKey("target_language")
    }

    val getSourceLanguageAsFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SOURCE_LANGUAGE_KEY].orEmpty()
    }

    suspend fun setSourceLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[SOURCE_LANGUAGE_KEY] = language
        }
    }

    val getTargetLanguageAsFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TARGET_LANGUAGE_KEY].orEmpty()
    }

    suspend fun setTargetLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[TARGET_LANGUAGE_KEY] = language
        }
    }
}