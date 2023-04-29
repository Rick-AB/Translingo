package com.example.translingo.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

val String.Companion.Empty
    inline get() = ""

fun Modifier.modifyIf(condition: Boolean, modify: Modifier.() -> Modifier): Modifier {
    return if (condition) modify() else this
}

fun Modifier.drawCustomIndicatorLine(
    indicatorBorder: BorderStroke,
    indicatorPadding: Dp = 0.dp
): Modifier {

    val strokeWidthDp = indicatorBorder.width
    return drawWithContent {
        drawContent()
        if (strokeWidthDp == Dp.Hairline) return@drawWithContent
        val strokeWidth = strokeWidthDp.value * density
        val y = size.height - strokeWidth / 2
        drawLine(
            indicatorBorder.brush,
            Offset((indicatorPadding).toPx(), y),
            Offset(size.width - indicatorPadding.toPx(), y),
            strokeWidth
        )
    }
}

fun Context.showLongToast(errorMessage: String) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showShortToast(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_LONG).show()
}

suspend fun <T> Channel<T>.sendUnique(value: T) {
    var exists = false
    for (element in this) {
        if (element is T) exists = true
    }

    if (!exists) send(value)
}