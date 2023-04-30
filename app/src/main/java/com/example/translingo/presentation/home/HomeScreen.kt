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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.translingo.R
import com.example.translingo.domain.model.Language
import com.example.translingo.presentation.languages.LanguageType
import com.example.translingo.presentation.navigation.Destinations
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.TranslingoTheme
import com.example.translingo.presentation.ui.theme.White
import com.kiwi.navigationcompose.typed.Destination

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeScreenState: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isTranslationActive = WindowInsets.isImeVisible
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeScreenTopAppBar(
                isTranslationActive = isTranslationActive
            ) { focusManager.clearFocus() }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
                .imePadding()
        ) {
            TranslationBody(
                homeScreenState = homeScreenState,
                onEvent = onEvent,
                modifier = Modifier
                    .weight(0.7f)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))
            LanguageButtons(
                modifier = Modifier.fillMaxWidth(),
                homeScreenState = homeScreenState,
                onEvent = onEvent,
                onNavigate = onNavigate
            )
        }
    }

}

@Composable
fun LanguageButtons(
    modifier: Modifier,
    homeScreenState: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val transition = updateTransition(targetState = homeScreenState, label = "buttonTransition")

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
            onNavigate(Destinations.SelectLanguage(LanguageType.SOURCE))
        }

        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            onEvent(
                HomeScreenEvent.OnSwapLanguages(
                    newSourceLanguageCode = homeScreenState.targetLanguage.languageCode,
                    newTargetLanguageCode = homeScreenState.sourceLanguage.languageCode
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
            onNavigate(Destinations.SelectLanguage(LanguageType.TARGET))
        }
    }
}

@Composable
fun LanguageButton(
    modifier: Modifier,
    languageType: LanguageType,
    transition: Transition<HomeScreenState>,
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
fun HomeScreenTopAppBar(isTranslationActive: Boolean, onBackArrowClick: () -> Unit) {
    AnimatedContent(
        targetState = isTranslationActive,
        transitionSpec = { fadeIn(tween(300)) with fadeOut(tween(500)) }
    ) { isTranslationActiveState ->
        if (isTranslationActiveState) ActiveTopAppBar(onBackArrowClick = onBackArrowClick)
        else DefaultTopAppBar()
    }
}

@Composable
fun DefaultTopAppBar() {
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
        navigationIcon = { TopAppBarIcon(imageVector = Icons.Default.Star) { } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}

@Composable
fun ActiveTopAppBar(onBackArrowClick: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            TopAppBarIcon(
                imageVector = Icons.Default.ArrowBack,
                onClick = onBackArrowClick
            )
        },
        actions = {
            TopAppBarIcon(imageVector = Icons.Default.History) { }
            TopAppBarIcon(imageVector = Icons.Default.MoreVert) { }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}

@Composable
fun TranslationBody(
    homeScreenState: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    modifier: Modifier
) {
    val (originalText, setOriginalText) = remember { mutableStateOf("") }
    Column(modifier = modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        BasicTextField(
            value = originalText,
            onValueChange = {
                setOriginalText(it)
                onEvent(HomeScreenEvent.OnTranslate(it))
            },
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
            text = homeScreenState.translatedText,
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
            mutableStateOf(HomeScreenState("HER", "Ella", source, target, false))
        }
        HomeScreen(state.value, {}, {})
    }
}