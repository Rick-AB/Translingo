package com.example.translingo.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.translingo.presentation.home.States
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
inline fun <reified T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycleScope.launch {
            flowWithLifecycle(
                lifecycleOwner.lifecycle,
                minActiveState
            ).collectIndexed { _, value -> action(value) }
        }
    }
}

val SwipeableState<States>.currentFraction: Float
    get() {
        val fraction = progress.fraction
        val initialValue = progress.from
        val targetValue = progress.to

        return when {
            initialValue == States.Collapsed && targetValue == States.Collapsed -> 0f
            initialValue == States.Expanded && targetValue == States.Expanded -> 1f
            initialValue == States.Collapsed && targetValue == States.Expanded -> fraction
            else -> 1f - fraction
        }
    }