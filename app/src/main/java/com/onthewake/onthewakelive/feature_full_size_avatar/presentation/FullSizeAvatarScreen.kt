package com.onthewake.onthewakelive.feature_full_size_avatar.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.components.StandardTopBar

@Composable
fun FullSizeAvatarScreen(
    navController: NavHostController,
    profilePictureUrl: String?
) {
    var isImageLoading by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) { isVisible = true }

    Scaffold(
        topBar = { StandardTopBar(onBackClicked = navController::popBackStack) }
    ) { paddingValues ->
        if (isImageLoading) StandardLoadingView()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = profilePictureUrl,
                onLoading = { isImageLoading = true },
                onError = { isImageLoading = false },
                onSuccess = { isImageLoading = false },
                contentDescription = stringResource(id = R.string.user_picture)
            )
        }
    }
}