package com.example.translingo.presentation.favorite

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.translingo.R
import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.ui.components.TopAppBarSearch
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.components.TopBarTitle
import com.example.translingo.presentation.ui.components.TranslationItem
import com.example.translingo.presentation.ui.theme.White
import com.example.translingo.util.Empty

@Composable
fun FavoriteScreen(
    uiState: FavoriteUiState,
    onEvent: (FavoriteEvent) -> Unit,
    goBack: () -> Unit,
    onItemClick: (Translation) -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    scrollBehavior.state.heightOffset

    var searchActive by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            FavoriteTopAppBar(
                titleRes = R.string.saved,
                searchActive = searchActive,
                canSearch = !uiState.isEmpty,
                searchQuery = uiState.searchQuery,
                scrollBehavior = scrollBehavior,
                onSearchActiveChange = { searchActive = it },
                onSearchChange = { onEvent(FavoriteEvent.OnSearchQueryChange(it)) },
                onNavigationIconClick = goBack
            )
        },
        containerColor = White,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            if (uiState.isEmpty) {
                EmptyFavoriteBody(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {
                    items(items = uiState.items, key = { it.id }) { item ->
                        TranslationItem(
                            translationItem = item,
                            shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            onFavoriteIconClick = { onEvent(FavoriteEvent.OnToggleFavorite(item.id)) }
                        ) { onItemClick(item) }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFavoriteBody(modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = Icons.Default.Star, contentDescription = "")

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.save_key_phrases),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(id = R.string.save_key_phrases_prompt),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FavoriteTopAppBar(
    @StringRes titleRes: Int,
    searchActive: Boolean,
    canSearch: Boolean,
    searchQuery: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchChange: (String) -> Unit,
    onNavigationIconClick: () -> Unit
) {
    LargeTopAppBar(
        title = {
            if (!searchActive) TopBarTitle(title = titleRes)
            else {
                TopAppBarSearch(
                    modifier = Modifier.fillMaxWidth(1f),
                    searchQuery = searchQuery,
                    text = String.Empty,
                    onSearchChange = onSearchChange
                )
            }
        },
        navigationIcon = {
            TopAppBarIcon(imageVector = Icons.Default.ArrowBack, onClick = onNavigationIconClick)
        },
        actions = {
            if (!searchActive && canSearch) {
                TopAppBarIcon(imageVector = Icons.Default.Search) {
                    onSearchActiveChange(true)
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}
