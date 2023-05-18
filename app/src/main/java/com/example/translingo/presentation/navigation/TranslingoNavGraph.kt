package com.example.translingo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.translingo.presentation.favorite.FavoriteScreen
import com.example.translingo.presentation.favorite.FavoriteViewModel
import com.example.translingo.presentation.history.HistoryViewModel
import com.example.translingo.presentation.home.HomeScreen
import com.example.translingo.presentation.home.HomeViewModel
import com.example.translingo.presentation.languages.SelectLanguageScreen
import com.example.translingo.presentation.languages.SelectLanguageViewModel
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.kiwi.navigationcompose.typed.popUpTo

@Composable
internal fun TranslingoNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = createRoutePattern<TranslingoDestinations.Home>()
    ) {
        composable<TranslingoDestinations.Home> {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                homeUiState = homeState,
                historyUiState = historyState,
                translationArg = translation,
                homeSideEffect = homeViewModel.sideEffect,
                onHomeEvent = homeViewModel::onEvent,
                onHistoryEvent = historyViewModel::onEvent,
                onNavigate = navController::navigate
            )
        }

        composable<TranslingoDestinations.SelectLanguage> {
            val viewModel: SelectLanguageViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = null)

            SelectLanguageScreen(
                uiState = uiState,
                languageType = languageType,
                sideEffect = sideEffect,
                onEvent = viewModel::onEvent
            ) { navController.popBackStack() }
        }

        composable<TranslingoDestinations.Favorite> {
            val viewModel: FavoriteViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            FavoriteScreen(
                goBack = { navController.popBackStack() },
                uiState = uiState,
                onEvent = viewModel::onEvent
            ) { translation ->
                navController.navigate(TranslingoDestinations.Home(translation)) {
                    popUpTo<TranslingoDestinations.Home> { inclusive = true }
                }
            }
        }
    }
}