package com.onthewake.onthewakelive.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.onthewake.onthewakelive.core.utils.Constants.DETAILS_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Constants.PICTURE_URL_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Constants.REGISTER_DATA_ARGUMENT_KEY
import com.onthewake.onthewakelive.feature_auth.presentation.auth_login.LoginScreen
import com.onthewake.onthewakelive.feature_auth.presentation.auth_otp.OtpScreen
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterScreen
import com.onthewake.onthewakelive.feature_full_size_avatar.presentation.FullSizeAvatarScreen
import com.onthewake.onthewakelive.feature_profile.presentation.edit_profile.EditProfileScreen
import com.onthewake.onthewakelive.feature_profile.presentation.profile.ProfileScreen
import com.onthewake.onthewakelive.feature_queue.presentation.queue_details.QueueItemDetailsScreen
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.QueueScreen
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.QueueViewModel
import com.onthewake.onthewakelive.feature_splash.presentation.components.ServerUnavailableScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestinationRoute: String,
    showSnackBar: (String) -> Unit
) {
    val queueViewModel: QueueViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestinationRoute
    ) {
        composable(route = Screen.ServerUnavailableScreen.route) { ServerUnavailableScreen() }
        authNavigation(navController = navController)
        mainNavigation(
            navController = navController,
            queueViewModel = queueViewModel,
            showSnackBar = showSnackBar
        )
    }
}

fun NavGraphBuilder.authNavigation(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.LoginScreen.route,
        route = NavigationRoute.AuthNavigation.route
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(
            route = Screen.OtpScreen.route,
            arguments = listOf(
                navArgument(REGISTER_DATA_ARGUMENT_KEY) { type = NavType.StringType }
            )
        ) {
            OtpScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.mainNavigation(
    navController: NavHostController,
    queueViewModel: QueueViewModel,
    showSnackBar: (String) -> Unit
) {
    navigation(
        startDestination = Screen.QueueScreen.route,
        route = NavigationRoute.MainNavigation.route
    ) {
        composable(route = Screen.QueueScreen.route) {
            QueueScreen(viewModel = queueViewModel, navController = navController)
        }
        composable(
            route = Screen.QueueDetailsScreen.route,
            arguments = listOf(navArgument(DETAILS_ARGUMENT_KEY) { type = NavType.StringType })
        ) {
            QueueItemDetailsScreen(navController = navController)
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = Screen.EditProfileScreen.route) {
            EditProfileScreen(navController = navController, showSnackBar = showSnackBar)
        }
        composable(
            route = Screen.FullSizeAvatarScreen.route,
            arguments = listOf(navArgument(PICTURE_URL_ARGUMENT_KEY) { type = NavType.StringType })
        ) { backStackEntry ->
            FullSizeAvatarScreen(
                navController = navController,
                profilePictureUrl = backStackEntry.arguments?.getString(PICTURE_URL_ARGUMENT_KEY)
            )
        }
    }
}