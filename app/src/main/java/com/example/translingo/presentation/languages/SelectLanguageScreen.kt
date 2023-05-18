package com.example.translingo.presentation.languages

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translingo.R
import com.example.translingo.domain.model.DownloadableLanguage
import com.example.translingo.domain.model.Language
import com.example.translingo.presentation.ui.components.LoadingDialog
import com.example.translingo.presentation.ui.components.TopAppBarIcon
import com.example.translingo.presentation.ui.components.TopAppBarSearch
import com.example.translingo.presentation.ui.components.TopBarTitle
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.ColombiaBlue
import com.example.translingo.presentation.ui.theme.White
import com.example.translingo.util.Empty
import com.example.translingo.util.modifyIf
import com.example.translingo.util.showLongToast

@Composable
fun SelectLanguageScreen(
    uiState: SelectLanguageUiState?,
    sideEffect: SelectLanguageSideEffect?,
    languageType: LanguageType,
    onEvent: (SelectLanguageEvent) -> Unit,
    goBack: () -> Unit
) {
    val context = LocalContext.current
    val title = if (languageType == LanguageType.SOURCE) R.string.translate_from
    else R.string.translate_to

    var searchActive by remember { mutableStateOf(false) }

    val actionGoBack: () -> Unit = {
        if (searchActive) searchActive = false

        val shouldShowToast =
            uiState?.savedSourceLanguage == null || uiState.savedTargetLanguage == null
        if (shouldShowToast) context.showLongToast(R.string.please_select_language)
        else goBack()
    }

    when (sideEffect) {
        SelectLanguageSideEffect.OnLanguageSelected -> goBack()
        null -> {}
    }

    BackHandler {
        if (searchActive) searchActive = false
        else actionGoBack()
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = White,
        topBar = {
            SelectLanguageTopAppBar(
                titleRes = title,
                searchActive = searchActive,
                searchQuery = uiState?.searchQuery ?: String.Empty,
                scrollBehavior = scrollBehavior,
                onSearchIconClick = {
                    scrollBehavior.state.heightOffset = -500f//workaround to collapse LargeTopAppbar
                    searchActive = true
                },
                onSearchChange = { onEvent(SelectLanguageEvent.OnSearchQueryChange(it)) },
                onNavigationIconClick = actionGoBack
            )
        }) {
        if (uiState != null) {
            val otherLanguage: Language?
            val savedLanguage: Language?
            when (languageType) {
                LanguageType.SOURCE -> {
                    savedLanguage = uiState.savedSourceLanguage
                    otherLanguage = uiState.savedTargetLanguage
                }

                LanguageType.TARGET -> {
                    savedLanguage = uiState.savedTargetLanguage
                    otherLanguage = uiState.savedSourceLanguage
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                SelectLanguageContent(
                    modifier = Modifier.padding(it),
                    languages = uiState.languages,
                    savedLanguage = savedLanguage,
                    onSelect = { languageCode ->
                        onEvent(
                            SelectLanguageEvent.OnSelectLanguage(
                                languageToSelectCode = languageCode,
                                currentSelectedLanguageCode = savedLanguage?.languageCode,
                                otherLanguageCode = otherLanguage?.languageCode,
                                languageType = languageType
                            )
                        )
                    }
                )

                if (uiState.loading) {
                    LoadingDialog()
                }
            }
        }
    }
}

@Composable
private fun SelectLanguageTopAppBar(
    @StringRes titleRes: Int,
    searchActive: Boolean,
    searchQuery: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchIconClick: () -> Unit,
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
                    titleRes = titleRes,
                    onSearchChange = onSearchChange
                )
            }
        },
        navigationIcon = {
            TopAppBarIcon(imageVector = Icons.Default.ArrowBack, onClick = onNavigationIconClick)
        },
        actions = {
            if (!searchActive) {
                TopAppBarIcon(imageVector = Icons.Default.Search, onClick = onSearchIconClick)
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
    )
}

@Composable
fun SelectLanguageContent(
    modifier: Modifier,
    languages: List<DownloadableLanguage>,
    savedLanguage: Language?,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier
    ) {
        allLanguagesSection(languages, savedLanguage, onSelect)
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    displayName: String,
    isSelected: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .modifyIf(isSelected) { background(ColombiaBlue, RoundedCornerShape(24.dp)) }
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "check",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        when {
            !isDownloaded && !isDownloading -> {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.download_icon),
                        contentDescription = null,
                        tint = Cerulean
                    )
                }
            }

            !isDownloaded && isDownloading -> {

            }
        }
    }
}


fun LazyListScope.allLanguagesSection(
    languages: List<DownloadableLanguage>,
    savedLanguage: Language?,
    onSelect: (String) -> Unit
) {
    item {
        Text(
            text = stringResource(id = R.string.all_languages).uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(color = Cerulean),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
    }

    items(languages) { item ->
        LanguageItem(
            modifier = Modifier.fillMaxWidth(),
            displayName = item.language.displayName,
            isSelected = item.language.languageCode == savedLanguage?.languageCode,
            isDownloaded = item.isDownloaded,
            isDownloading = item.isDownloading
        ) {
            onSelect(item.language.languageCode)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPrev() {
    SelectLanguageTopAppBar(
        titleRes = R.string.translate_from,
        true,
        "",
        TopAppBarDefaults.enterAlwaysScrollBehavior(),
        {},
        {}) {

    }
}

@Preview
@Composable
fun LoadingDialogPrev() {
    LoadingDialog()
}