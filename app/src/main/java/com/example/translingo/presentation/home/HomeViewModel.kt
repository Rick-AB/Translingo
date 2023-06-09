package com.example.translingo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translingo.domain.model.Language
import com.example.translingo.domain.repository.LanguageRepository
import com.example.translingo.domain.repository.TranslationRepository
import com.example.translingo.presentation.languages.LanguageType
import com.example.translingo.util.Empty
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import com.example.translingo.domain.model.Translation as DomainModelTranslation

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val languageRepository: LanguageRepository,
    private val translationRepository: TranslationRepository
) : ViewModel() {
    private var job: Job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    private val sourceLanguageFlow = languageRepository.getSourceLanguageAsFlow()
    private val targetLanguageFlow = languageRepository.getTargetLanguageAsFlow()
    private val builder = TranslatorOptions.Builder()
    private val translatorStateAsFlow = combine(
        sourceLanguageFlow,
        targetLanguageFlow,
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
    private val translatedTextAsFlow = originalTextAsFlow
        .debounce(700.milliseconds)
        .combine(translatorStateAsFlow) { originalText, translatorState ->
            if (translatorState == null) return@combine String.Empty
            else {
                if (originalText.isEmpty() || originalText.isBlank()) return@combine String.Empty

                val translator = translatorState.translator
                translator.downloadModelIfNeeded().await()
                val translatedText = translator.translate(originalText).await()
                saveTranslation(
                    originalText,
                    translatedText,
                    translatorState.sourceLanguage,
                    translatorState.targetLanguage
                )

                translatedText
            }
        }

    val uiState =
        combine(
            originalTextAsFlow,
            translatedTextAsFlow,
            sourceLanguageFlow,
            targetLanguageFlow
        ) { originalText, translatedText, sourceLanguage, targetLanguage ->
            if (sourceLanguage == null || targetLanguage == null) return@combine HomeUiState.default()

            HomeUiState(
                originalText = originalText,
                translatedText = translatedText,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                loading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), HomeUiState.default())

    private val sideEffectChannel = Channel<HomeSideEffect>()
    val sideEffect = sideEffectChannel.receiveAsFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnTranslate -> originalTextAsFlow.update { event.text }
            is HomeEvent.OnSwapLanguages -> swapLanguages(
                event.newSourceLanguageCode,
                event.newTargetLanguageCode
            )

            HomeEvent.OnForeground -> checkLanguages()
        }
    }

    private fun checkLanguages() {
        viewModelScope.launch {
            val sourceLanguage = languageRepository.getSourceLanguage()
            if (sourceLanguage == null) {
                sideEffectChannel.send(HomeSideEffect.SelectLanguage(LanguageType.SOURCE))
                return@launch
            }

            val targetLanguage = languageRepository.getTargetLanguage()
            if (targetLanguage == null)
                sideEffectChannel.send(HomeSideEffect.SelectLanguage(LanguageType.TARGET))
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

    private fun saveTranslation(
        originalText: String,
        translatedText: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ) {
        job.cancel()
        viewModelScope.launch(job) {
            delay(1300.milliseconds) // allow time for user to continue typing
            val id =
                ("${sourceLanguage.languageCode}-${targetLanguage.languageCode}"
                        + originalText.lowercase() + translatedText.lowercase()).hashCode()

            val translation = DomainModelTranslation(
                id = id,
                originalText = originalText,
                translatedText = translatedText,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                isFavorite = false,
                date = LocalDate.now().toString()
            )

            translationRepository.saveTranslation(translation)
        }
    }


    data class TranslatorState(
        val translator: Translator,
        val sourceLanguage: Language,
        val targetLanguage: Language,
    )
}