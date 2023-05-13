package com.example.translingo.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.translingo.domain.model.Translation
import com.example.translingo.presentation.ui.theme.Cerulean
import com.example.translingo.presentation.ui.theme.White

@Composable
fun TranslationItem(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    translationItem: Translation,
    onFavoriteIconClick: () -> Unit,
    onItemClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = shape,
        elevation = CardDefaults.cardElevation(elevation),
        onClick = onItemClick,
        modifier = modifier.padding(top = 12.dp, bottom = 8.dp, start = 24.dp, end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translationItem.originalText,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = translationItem.translatedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onFavoriteIconClick) {
                if (translationItem.isFavorite) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "",
                        tint = Cerulean
                    )
                } else Icon(imageVector = Icons.Outlined.StarBorder, contentDescription = "")
            }
        }
    }

}