package com.example.translingo.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.translingo.R
import com.example.translingo.domain.model.Language
import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.history.HistoryScreen
import com.example.translingo.presentation.languages.LanguageType
import com.example.translingo.presentation.navigation.TranslingoDestinations
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.TranslingoTheme
import com.example.translingo.presentation.ui.theme.White
import com.example.translingo.util.observeWithLifecycle
import com.kiwi.navigationcompose.typed.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    translationArg: Translation?,
    homeSideEffect: Flow<HomeSideEffect>,
    onEvent: (HomeEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isTranslationActive = WindowInsets.isImeVisible
    val clearText: () -> Unit = { onEvent(HomeEvent.OnTranslate("")) }

    LaunchedEffect(key1 = Unit) {
        if (translationArg != null) {
            onEvent(HomeEvent.OnTranslate(translationArg.originalText))
        }
        onEvent(HomeEvent.OnForeground)
    }

    homeSideEffect.observeWithLifecycle {
        when (it) {
            is HomeSideEffect.SelectLanguage -> {
                delay(500.milliseconds)
                onNavigate(TranslingoDestinations.SelectLanguage(it.languageType))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeScreenTopAppBar(
                isTranslationActive = isTranslationActive,
                isTranslationEmpty = homeUiState.originalText.isEmpty(),
                onBackArrowClick = {
                    clearText()
                    focusManager.clearFocus()
                },
                onClearIconClick = clearText,
                onHistoryIconClick = {},
                onFavoriteIconClick = { onNavigate(TranslingoDestinations.Favorite) }
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = innerPadding.calculateTopPadding())

        ) {

            val height = remember(isTranslationActive) {
                if (isTranslationActive) maxHeight.times(0.6f)
                else maxHeight.times(0.7f)
            }
            val animatedHeight by animateDpAsState(targetValue = height, animationSpec = tween(150))

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                val (surface, languageButtons, anchor1, anchor2) = createRefs()
                val barrier = createTopBarrier(anchor1, anchor2)
                val guideLine = createGuidelineFromTop(0.8f)
                Surface(
                    color = White,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    modifier = Modifier.constrainAs(surface) {
                        top.linkTo(parent.top)
                        bottom.linkTo(languageButtons.top, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        this.height = Dimension.fillToConstraints
                    }
                ) {
//                    HistoryScreen()
                    TranslationBody(
                        originalText = homeUiState.originalText,
                        translatedText = homeUiState.translatedText,
                        modifier = Modifier
                    ) { onEvent(HomeEvent.OnTranslate(it)) }
                }

                LanguageButtons(
                    modifier = Modifier.constrainAs(languageButtons) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(barrier)
                    },
                    homeUiState = homeUiState,
                    onEvent = onEvent,
                    onNavigate = onNavigate
                )

                Box(modifier = Modifier.constrainAs(anchor2) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    this.height = Dimension.value(1.dp)
                })

                if (!isTranslationActive) {
                    Box(modifier = Modifier
                        .constrainAs(anchor1) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(guideLine)
                            this.height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun LanguageButtons(
    modifier: Modifier,
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val transition = updateTransition(targetState = homeUiState, label = "buttonTransition")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LanguageButton(
            modifier = Modifier
                .padding(start = 24.dp)
                .height(50.dp)
                .weight(1f),
            languageType = LanguageType.SOURCE,
            transition = transition
        ) {
            onNavigate(TranslingoDestinations.SelectLanguage(LanguageType.SOURCE))
        }

        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            onEvent(
                HomeEvent.OnSwapLanguages(
                    newSourceLanguageCode = homeUiState.targetLanguage.languageCode,
                    newTargetLanguageCode = homeUiState.sourceLanguage.languageCode
                )
            )
        }) {
            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "", tint = Color.Black)
        }

        Spacer(modifier = Modifier.width(8.dp))
        LanguageButton(
            modifier = Modifier
                .padding(end = 24.dp)
                .height(50.dp)
                .weight(1f),
            languageType = LanguageType.TARGET,
            transition,
        ) {
            onNavigate(TranslingoDestinations.SelectLanguage(LanguageType.TARGET))
        }
    }
}

@Composable
fun LanguageButton(
    modifier: Modifier,
    languageType: LanguageType,
    transition: Transition<HomeUiState>,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = White),
        shape = RoundedCornerShape(16.dp)
    ) {
        transition.AnimatedContent(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(),
            transitionSpec = {
                if (languageType == LanguageType.SOURCE) slideInHorizontally(tween(500)) { fullWidth -> fullWidth * 2 } with
                        slideOutHorizontally(tween(300)) { fullWidth -> fullWidth * 2 }
                else slideInHorizontally(tween(500)) { fullWidth -> -fullWidth * 2 } with
                        slideOutHorizontally(tween(300)) { fullWidth -> -fullWidth * 2 }
            },
        ) { targetState ->
            Text(
                text = if (languageType == LanguageType.SOURCE) targetState.sourceLanguage.displayName else targetState.targetLanguage.displayName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black,
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

@Composable
fun HomeScreenTopAppBar(
    isTranslationActive: Boolean,
    isTranslationEmpty: Boolean,
    onBackArrowClick: () -> Unit,
    onClearIconClick: () -> Unit,
    onHistoryIconClick: () -> Unit,
    onFavoriteIconClick: () -> Unit
) {
    AnimatedContent(
        targetState = isTranslationActive,
        transitionSpec = { fadeIn(tween(300)) with fadeOut(tween(500)) }
    ) { isTranslationActiveState ->
        if (isTranslationActiveState) {
            ActiveTopAppBar(
                isTranslationEmpty = isTranslationEmpty,
                onBackArrowClick = onBackArrowClick,
                onClearClick = onClearIconClick,
                onHistoryClick = onHistoryIconClick
            )
        } else DefaultTopAppBar(onFavoriteIconClick)
    }
}

@Composable
fun DefaultTopAppBar(onFavoriteIconClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        navigationIcon = {
            TopAppBarIcon(
                imageVector = Icons.Default.Star,
                onClick = onFavoriteIconClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}

@Composable
fun ActiveTopAppBar(
    isTranslationEmpty: Boolean,
    onBackArrowClick: () -> Unit,
    onClearClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            TopAppBarIcon(
                imageVector = Icons.Default.ArrowBack,
                onClick = onBackArrowClick
            )
        },
        actions = {
            if (isTranslationEmpty) {
                TopAppBarIcon(
                    imageVector = Icons.Default.History,
                    onClick = onHistoryClick
                )
            } else TopAppBarIcon(imageVector = Icons.Default.Close, onClick = onClearClick)

            TopAppBarIcon(imageVector = Icons.Default.MoreVert) { }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}

@Composable
fun TranslationBody(
    originalText: String,
    translatedText: String,
    modifier: Modifier,
    onTextChange: (String) -> Unit,
) {
    Column(modifier = modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        BasicTextField(
            value = originalText,
            onValueChange = onTextChange,
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (originalText.isEmpty()) {
                            Text(
                                text = "Enter text",
                                style = MaterialTheme.typography.titleLarge.copy(Color.DarkGray)
                            )
                        }

                        innerTextField()
                    }
                }
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.fillMaxWidth()
        )

        if (originalText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = translatedText,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Cerulean,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePrev() {
    TranslingoTheme {
        val source = Language("en", "English")
        val target = Language("es", "Spanish")
        val state = remember {
            mutableStateOf(HomeUiState("HER", "Ella", source, target, false))
        }
        HomeScreen(state.value, null, emptyFlow(), {}, {})
    }
}