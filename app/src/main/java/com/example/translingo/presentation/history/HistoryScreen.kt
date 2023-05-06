package com.example.translingo.presentation.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.translingo.domain.model.History
import com.example.translingo.presentation.ui.components.DefaultSwipeActionsConfig
import com.example.translingo.presentation.ui.components.SwipeActions
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.White
import kotlin.math.absoluteValue

@Composable
fun HistoryScreen() {
    val viewModel: HistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        HistoryScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }
}

@Composable
fun HistoryScreen(uiState: HistoryUiState, onEvent: (HistoryEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            items(
                items = uiState.items,
                key = {
                    when (it) {
                        is HistoryItem.Header -> it.text
                        is HistoryItem.Item -> it.history.id
                    }
                }
            ) { item ->
                val deleteSwipeActionConfig = DefaultSwipeActionsConfig.copy(
                    stayDismissed = true,
                    onDismiss = {
                        onEvent(HistoryEvent.OnDeleteHistory((item as HistoryItem.Item).history))
                    }
                )
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

                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(elevation)
                        ) {
                            HistoryItem(historyItem = item.history)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun HistoryItem(modifier: Modifier = Modifier, historyItem: History) {
    Row(
        modifier = modifier
            .clickable { }
            .padding(top = 12.dp, bottom = 8.dp, start = 24.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = historyItem.originalText,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = historyItem.translatedText, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Outlined.StarBorder, contentDescription = "")
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

@Preview(showBackground = true)
@Composable
fun HistoryPrev() {
    MaterialTheme {
        val dummy = (1..10).map {
            HistoryItem.Item(
                History(it, "I am him", "Yo soy el", null, null, "2023-05-05")
            )
        }
        val state = HistoryUiState(dummy)
        HistoryScreen(state) {}
    }
}