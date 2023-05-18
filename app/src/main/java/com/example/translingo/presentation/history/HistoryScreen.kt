package com.example.translingo.presentation.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translingo.R
import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.ui.components.DefaultSwipeActionsConfig
import com.example.translingo.presentation.ui.components.SwipeActions
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.components.TranslationItem
import com.example.translingo.presentation.ui.theme.Cerulean
import kotlin.math.absoluteValue

@Composable
fun HistoryScreen(
    modifier: Modifier,
    listState: LazyListState,
    uiState: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onHistoryItemClick: (Translation) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        val items = remember(uiState.items) { uiState.items }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            reverseLayout = true
        ) {
            items(
                items = items,
                key = {
                    when (it) {
                        is HistoryItem.Header -> it.text
                        is HistoryItem.Item -> it.translation.id
                    }
                }
            ) { item ->
                val deleteSwipeActionConfig = remember(item) {
                    DefaultSwipeActionsConfig.copy(
                        stayDismissed = true,
                        onDismiss = {
                            onEvent(HistoryEvent.OnDeleteHistory((item as HistoryItem.Item).translation))
                        }
                    )
                }

                when (item) {
                    is HistoryItem.Header -> HistoryDateHeader(
                        modifier = Modifier.padding(start = 24.dp),
                        date = item.text
                    )

                    is HistoryItem.Item -> SwipeActions(
                        startActionsConfig = deleteSwipeActionConfig,
                        endActionsConfig = deleteSwipeActionConfig,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement()
                    ) { state ->
                        val animateCorners by remember {
                            derivedStateOf {
                                state.progress.absoluteValue > 30
                            }
                        }

                        val elevation by animateDpAsState(
                            targetValue = when {
                                animateCorners -> 6.dp
                                else -> 0.dp
                            }
                        )

                        TranslationItem(
                            translationItem = item.translation,
                            elevation = elevation,
                            onFavoriteIconClick = { onEvent(HistoryEvent.OnToggleFavorite(item.translation.id)) }
                        ) {
                            onHistoryItemClick(item.translation)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDateHeader(modifier: Modifier, date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.bodySmall.copy(
            color = Cerulean,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = modifier
    )
}

@Composable
fun HistoryTopAppBar(modifier: Modifier, onNavIconClick: () -> Unit) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.history),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        },
        navigationIcon = {
            TopAppBarIcon(
                imageVector = Icons.Default.ArrowBack,
                onClick = onNavIconClick
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryPrev() {
    MaterialTheme {
        val dummy = (1..10).map {
            HistoryItem.Item(
                Translation(
                    it,
                    "I am him",
                    "Yo soy el",
                    null,
                    null,
                    isFavorite = true,
                    "2023-05-05"
                )
            )
        }
        val state = HistoryUiState(dummy)
        HistoryScreen(modifier = Modifier, rememberLazyListState(), state, {}) {}
    }
}