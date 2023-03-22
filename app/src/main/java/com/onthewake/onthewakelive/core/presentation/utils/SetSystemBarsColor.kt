package com.onthewake.onthewakelive.core.presentation.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetSystemBarsColor(
    systemBarsColor: Color? = null,
    statusBarColor: Color? = null,
    navigationBarColor: Color? = null
) {
    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        if (systemBarsColor != null) systemUiController.setSystemBarsColor(
            color = systemBarsColor, darkIcons = !darkTheme
        )
        else if (statusBarColor != null && navigationBarColor != null) {
            systemUiController.setStatusBarColor(
                color = statusBarColor, darkIcons = !darkTheme
            )
            systemUiController.setNavigationBarColor(
                color = navigationBarColor, darkIcons = !darkTheme
            )
        }
    }
}