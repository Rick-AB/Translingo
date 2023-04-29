package com.example.translingo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.translingo.presentation.home.HomeScreen
import com.example.translingo.presentation.home.HomeScreenEvent
import com.example.translingo.presentation.home.HomeScreenSideEffect
import com.example.translingo.presentation.home.HomeViewModel
import com.example.translingo.presentation.languages.SelectLanguage
import com.example.translingo.presentation.languages.SelectLanguageSideEffect
import com.example.translingo.presentation.languages.SelectLanguageViewModel
import com.example.translingo.util.observeWithLifecycle
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun TranslingoNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = createRoutePattern<Destinations.Home>()
    ) {
        composable<Destinations.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                viewModel.onEvent(HomeScreenEvent.OnForeground)
                viewModel.sideEffect.collect {
                    when (it) {
                        is HomeScreenSideEffect.SelectLanguage -> {
                            delay(500.milliseconds)
                            navController.navigate(Destinations.SelectLanguage(it.languageType))
                        }
                    }
                }
            }

            HomeScreen(uiState, viewModel::onEvent, navController::navigate)
        }

        composable<Destinations.SelectLanguage> {
            val viewModel: SelectLanguageViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            viewModel.sideEffectChannel.receiveAsFlow().observeWithLifecycle {
                when (it) {
                    SelectLanguageSideEffect.OnLanguageSelected -> navController.popBackStack()
                }
            }

            SelectLanguage(
                uiState = uiState,
                languageType = this.languageType,
                onEvent = viewModel::onEvent
            ) { navController.navigateUp() }
        }
    }
}