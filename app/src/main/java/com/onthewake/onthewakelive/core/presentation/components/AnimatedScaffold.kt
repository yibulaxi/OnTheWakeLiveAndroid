package com.onthewake.onthewakelive.core.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedScaffold(
    isLoading: Boolean,
    topBar: @Composable () -> Unit = {},
    snackBarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AnimatedContent(targetState = isLoading) { loading ->
        if (loading) StandardLoadingView()
        else Scaffold(topBar = topBar, snackbarHost = snackBarHost, content = content)
    }
}