package com.onthewake.onthewakelive.core.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedScaffold(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackBarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AnimatedContent(targetState = isLoading) { loading ->
        if (loading) StandardLoadingView()
        else Scaffold(
            modifier = modifier,
            topBar = topBar,
            snackbarHost = snackBarHost,
            bottomBar = bottomBar,
            content = content
        )
    }
}