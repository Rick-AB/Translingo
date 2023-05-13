package com.example.translingo.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.translingo.util.drawCustomIndicatorLine

@Composable
fun TopAppBarPlaceholder(@StringRes titleRes: Int, showEllipse: Boolean = true) {
    val text =
        if (showEllipse) stringResource(id = titleRes).plus("...") else stringResource(id = titleRes)
    TopAppBarPlaceholder(text = text)
}

@Composable
fun TopAppBarPlaceholder(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(Color.LightGray)
        )
    }
}

@Composable
fun TopAppBarSearch(
    modifier: Modifier,
    searchQuery: String,
    @StringRes titleRes: Int,
    onSearchChange: (String) -> Unit
) {
    TopAppBarSearch(
        modifier = modifier,
        searchQuery = searchQuery,
        text = stringResource(id = titleRes),
        onSearchChange = onSearchChange
    )
}

@Composable
fun TopAppBarSearch(
    modifier: Modifier,
    searchQuery: String,
    text: String,
    onSearchChange: (String) -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        modifier = modifier.drawCustomIndicatorLine(BorderStroke(0.5.dp, Color.LightGray)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Blue
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { TopAppBarPlaceholder(text) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) TopAppBarIcon(imageVector = Icons.Default.Close) {
                onSearchChange("")
            }
        },
    )
}

@Composable
fun TopBarTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TopBarTitle(@StringRes title: Int) {
    TopBarTitle(title = stringResource(id = title))
}

@Composable
fun TopAppBarIcon(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = imageVector, contentDescription = null)
    }
}