package com.onthewake.onthewakelive.feature_full_size_avatar.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.components.StandardTopBar
import com.onthewake.onthewakelive.core.presentation.utils.SetSystemBarsColor

@Composable
fun FullSizeAvatarScreen(
    navController: NavHostController,
    imageLoader: ImageLoader,
    profilePictureUrl: String?
) {
    var isImageLoading by remember { mutableStateOf(false) }

    SetSystemBarsColor(systemBarsColor = MaterialTheme.colorScheme.background)

    Scaffold(
        topBar = { StandardTopBar(onBackClicked = { navController.popBackStack() }) }
    ) { paddingValues ->
        if (isImageLoading) StandardLoadingView()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(
                    model = profilePictureUrl,
                    imageLoader = imageLoader,
                    onLoading = { isImageLoading = true },
                    onError = { isImageLoading = false },
                    onSuccess = { isImageLoading = false }
                ),
                contentDescription = stringResource(id = R.string.user_picture)
            )
        }
    }
}