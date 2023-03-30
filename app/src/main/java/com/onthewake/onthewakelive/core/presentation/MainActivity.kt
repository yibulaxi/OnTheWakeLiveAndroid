package com.onthewake.onthewakelive.core.presentation

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import coil.imageLoader
import com.onthewake.onthewakelive.core.presentation.components.StandardScaffold
import com.onthewake.onthewakelive.core.presentation.ui.theme.OnTheWakeLiveTheme
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.Constants.ADMIN_IDS
import com.onthewake.onthewakelive.feature_splash.presentation.SplashViewModel
import com.onthewake.onthewakelive.navigation.SetupNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            splashViewModel.state.value.isLoading
        }

        setContent {
            OnTheWakeLiveTheme {
                val navController = rememberNavController()
                val isUserAdmin = preferences.getString(Constants.PREFS_USER_ID, null) in ADMIN_IDS

                val state = splashViewModel.state.value

                state.startDestination?.let { startDestination ->
                    StandardScaffold(navController = navController, isUserAdmin = isUserAdmin) {
                        SetupNavGraph(
                            navController = navController,
                            imageLoader = imageLoader,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}