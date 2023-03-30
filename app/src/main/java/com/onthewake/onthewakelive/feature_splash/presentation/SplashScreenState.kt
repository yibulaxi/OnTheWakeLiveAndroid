package com.onthewake.onthewakelive.feature_splash.presentation

import com.onthewake.onthewakelive.navigation.Screen

data class SplashScreenState(
    val startDestination: Screen? = null,
    val isLoading: Boolean = false
)