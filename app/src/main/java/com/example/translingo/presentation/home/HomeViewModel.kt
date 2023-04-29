package com.example.translingo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translingo.domain.model.Language
import com.example.translingo.domain.repository.LanguageRepository
import com.example.translingo.presentation.languages.LanguageType
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {
    private val sourceLanguageFlow = languageRepository.getSourceLanguageAsFlow()
    private val targetLanguageFlow = languageRepository.getTargetLanguageAsFlow()

    private val builder = TranslatorOptions.Builder()
    private val translatorStateAsFlow = combine(
        sourceLanguageFlow,
        targetLanguageFlow
    ) { sourceLanguage, targetLanguage ->
        if (sourceLanguage == null || targetLanguage == null) return@combine null
        else {
            val sourceLanguageCode =
                findLanguageFromLibrary(sourceLanguage.languageCode) ?: TranslateLanguage.ENGLISH
            val targetLanguageCode =
                findLanguageFromLibrary(targetLanguage.languageCode) ?: TranslateLanguage.SPANISH

            val options = builder.setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build()

            val translator = Translation.getClient(options)
            TranslatorState(translator, sourceLanguage, targetLanguage)
        }
    }

    private val originalTextAsFlow = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    val uiState = originalTextAsFlow
        .debounce(700.milliseconds)
        .combine(
            translatorStateAsFlow,
        ) { originalText, translatorState ->
            if (translatorState == null) return@combine HomeScreenState.default()
            else {
                val translator = translatorState.translator
                translator.downloadModelIfNeeded().await()
                val translatedText = translator.translate(originalText).await()

                HomeScreenState(
                    originalText = originalText,
                    translatedText = translatedText,
                    sourceLanguage = translatorState.sourceLanguage,
                    targetLanguage = translatorState.targetLanguage
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), HomeScreenState.default())

    private val sideEffectChannel = Channel<HomeScreenSideEffect>()
    val sideEffect = sideEffectChannel.receiveAsFlow()

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnTranslate -> originalTextAsFlow.update { event.text }
            is HomeScreenEvent.OnSwapLanguages -> swapLanguages(
                event.newSourceLanguageCode,
                event.newTargetLanguageCode
            )

            HomeScreenEvent.OnForeground -> checkLanguages()
        }
    }

    private fun checkLanguages() {
        viewModelScope.launch {
            val sourceLanguage = languageRepository.getSourceLanguage()
            if (sourceLanguage == null)
                sideEffectChannel.send(HomeScreenSideEffect.SelectLanguage(LanguageType.SOURCE))

            val targetLanguage = languageRepository.getTargetLanguage()
            if (targetLanguage == null)
                sideEffectChannel.send(HomeScreenSideEffect.SelectLanguage(LanguageType.TARGET))
        }
    }

    private fun swapLanguages(newSourceLanguageCode: String, newTargetLanguageCode: String) {
        viewModelScope.launch {
            launch {
                languageRepository.setSourceLanguage(newSourceLanguageCode)
            }
            launch {
                languageRepository.setTargetLanguage(newTargetLanguageCode)
            }
        }
    }

    private fun findLanguageFromLibrary(language: String?): String? {
        val allLanguages = TranslateLanguage.getAllLanguages()
        return allLanguages.find { it == language }
    }

    data class TranslatorState(
        val translator: Translator,
        val sourceLanguage: Language,
        val targetLanguage: Language
    )
}