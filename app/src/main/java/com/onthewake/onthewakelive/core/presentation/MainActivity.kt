package com.onthewake.onthewakelive.core.presentation

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.onthewake.onthewakelive.core.presentation.components.StandardScaffold
import com.onthewake.onthewakelive.core.presentation.ui.theme.OnTheWakeLiveTheme
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.core.utils.isUserAdmin
import com.onthewake.onthewakelive.feature_queue.domain.repository.QueueSocketService
import com.onthewake.onthewakelive.feature_splash.presentation.SplashViewModel
import com.onthewake.onthewakelive.navigation.SetupNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var queueSocketService: QueueSocketService

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().setKeepOnScreenCondition {
            splashViewModel.state.value.isLoading
        }

        setContent {
            OnTheWakeLiveTheme {
                val navController = rememberNavController()
                val userId = preferences.getString(Constants.PREFS_USER_ID, null)
                val state = splashViewModel.state.value

                state.startDestinationRoute?.let { startDestinationRoute ->
                    StandardScaffold(
                        navController = navController,
                        isUserAdmin = userId.isUserAdmin()
                    ) {
                        SetupNavGraph(
                            navController = navController,
                            startDestinationRoute = startDestinationRoute
                        )
                    }
                }
            }
        }
    }
}