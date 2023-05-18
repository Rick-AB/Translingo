package com.example.translingo.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.translingo.R
import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.history.HistoryEvent
import com.example.translingo.presentation.history.HistoryScreen
import com.example.translingo.presentation.history.HistoryTopAppBar
import com.example.translingo.presentation.history.HistoryUiState
import com.example.translingo.presentation.languages.LanguageType
import com.example.translingo.presentation.navigation.TranslingoDestinations
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.White
import com.example.translingo.util.currentFraction
import com.example.translingo.util.keyboardAsState
import com.kiwi.navigationcompose.typed.Destination
import kotlinx.coroutines.launch

enum class States { Expanded, Collapsed }
enum class ViewState { History, TranslationIdle, TranslationActive, TranslationDone }

@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    historyUiState: HistoryUiState,
    translationArg: Translation?,
    onHomeEvent: (HomeEvent) -> Unit,
    onHistoryEvent: (HistoryEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isKeyboardVisible by keyboardAsState()
    val clearText: () -> Unit = { onHomeEvent(HomeEvent.OnTranslate("")) }
    val swipeableState = rememberSwipeableState(States.Collapsed)


    val activeView by remember {
        derivedStateOf {
            when {
                isKeyboardVisible -> ViewState.TranslationActive
                !isKeyboardVisible && homeUiState.originalText.isNotEmpty() -> ViewState.TranslationDone
                swipeableState.currentFraction <= 0.5f -> ViewState.TranslationIdle
                else -> ViewState.History
            }
        }
    }

    val getTranslationAlpha: () -> Float = {
        val currentFraction = swipeableState.currentFraction
        if (currentFraction <= 0.5f) {
            1.minus(currentFraction.times(2)) // fully invisible halfway through the animation
        } else {
            0f
        }
    }
    val getHistoryAlpha: () -> Float = {
        val currentFraction = swipeableState.currentFraction
        if (currentFraction <= 0.45f) {
            0f
        } else {
            convertAlphaRange(currentFraction, 0.5f..1f, 0f..1f)
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (translationArg != null) {
            onHomeEvent(HomeEvent.OnTranslate(translationArg.originalText))
        }
        onHomeEvent(HomeEvent.OnForeground)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (activeView == ViewState.History) {
                HistoryTopAppBar(modifier = Modifier.graphicsLayer { alpha = getHistoryAlpha() }) {
                    scope.launch { swipeableState.snapTo(States.Collapsed) }
                }
            } else {
                HomeScreenTopAppBar(
                    modifier = Modifier.graphicsLayer { alpha = getTranslationAlpha() },
                    isTranslationActive = isKeyboardVisible,
                    isTranslationEmpty = homeUiState.originalText.isEmpty(),
                    onBackArrowClick = {
                        clearText()
                        focusManager.clearFocus()
                    },
                    onClearIconClick = clearText,
                    onHistoryIconClick = {
                        scope.launch {
                            focusManager.clearFocus()
                            swipeableState.snapTo(States.Expanded)
                        }
                    },
                    onFavoriteIconClick = { onNavigate(TranslingoDestinations.Favorite) }
                )
            }
        },
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()

        ) {
            val density = LocalDensity.current
            val surfaceMinHeight = maxHeight.times(0.7f)
            val minHeightInPx = with(density) { surfaceMinHeight.toPx() }
            val maxHeightInPx = with(density) { maxHeight.toPx() }
            val historyListState = rememberLazyListState()
            val anchors =
                mapOf(
                    minHeightInPx to States.Collapsed,
                    maxHeightInPx to States.Expanded
                )

            val height by remember {
                derivedStateOf { with(density) { swipeableState.offset.value.toDp() } }
            }

            val topPadding = surfaceMinHeight.plus(24.dp)
            LanguageButtons(
                modifier = Modifier
                    .padding(top = topPadding)
                    .fillMaxWidth(),
                homeUiState = homeUiState,
                onEvent = onHomeEvent,
                onNavigate = onNavigate
            )


            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        swipeableState.performFling(velocity = available.y)
                        return super.onPostFling(consumed, available)
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        return Offset(0f, swipeableState.performDrag(delta))
                    }

                    override suspend fun onPreFling(available: Velocity): Velocity {
                        return if (available.y < 0) {
                            swipeableState.performFling(available.y)
                            available
                        } else {
                            available
                        }
                    }

                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        return if (delta > 0) Offset(0f, swipeableState.performDrag(delta))
                        else Offset.Zero
                    }
                }
            }

            Column(modifier = Modifier.imePadding()) {
                Surface(
                    color = White,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .then(
                            if (activeView == ViewState.TranslationIdle || activeView == ViewState.History) {
                                Modifier
                                    .swipeable(
                                        state = swipeableState,
                                        anchors = anchors,
                                        orientation = Orientation.Vertical,
                                        thresholds = { _, _ -> FractionalThreshold(0.5f) }
                                    )
                                    .nestedScroll(nestedScrollConnection)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Box {
                        when (activeView) {
                            ViewState.History -> {
                                HistoryScreen(
                                    uiState = historyUiState,
                                    onEvent = onHistoryEvent,
                                    listState = historyListState,
                                    onHistoryItemClick = {
                                        onHomeEvent(HomeEvent.OnTranslate(it.originalText))
                                        scope.launch { swipeableState.snapTo(States.Collapsed) }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            alpha = getHistoryAlpha()
                                        }
                                )
                            }

                            else -> {
                                TranslationBody(
                                    originalText = homeUiState.originalText,
                                    translatedText = homeUiState.translatedText,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = getTranslationAlpha()
                                    }
                                ) { onHomeEvent(HomeEvent.OnTranslate(it)) }
                            }
                        }

                        SurfaceIndicator(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SurfaceIndicator(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50),
            )
            .size(width = 36.dp, height = 4.dp),
    )
}

@Composable
fun LanguageButtons(
    modifier: Modifier,
    homeUiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val targetState =
        remember(homeUiState.targetLanguage, homeUiState.sourceLanguage) { homeUiState }

    val transition = updateTransition(targetState = targetState, label = "buttonTransition")

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
    modifier: Modifier,
    isTranslationActive: Boolean,
    isTranslationEmpty: Boolean,
    onBackArrowClick: () -> Unit,
    onClearIconClick: () -> Unit,
    onHistoryIconClick: () -> Unit,
    onFavoriteIconClick: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
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

private fun convertAlphaRange(
    value: Float,
    originalRange: ClosedRange<Float>,
    targetRange: ClosedRange<Float>
): Float {
    val ratio = (value - originalRange.start) / (originalRange.endInclusive - originalRange.start)
    return (ratio * (targetRange.endInclusive - targetRange.start))
}
