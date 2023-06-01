package com.onthewake.onthewakelive.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.onthewake.onthewakelive.feature_splash.presentation.components.ServerUnavailableScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(route = Screen.ServerUnavailableScreen.route) {
            ServerUnavailableScreen()
        }
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
        composable(route = Screen.QueueScreen.route) {
            QueueScreen(navController = navController)
        }
        composable(
            route = Screen.QueueDetailsScreen.route,
            arguments = listOf(
                navArgument(DETAILS_ARGUMENT_KEY) { type = NavType.StringType }
            )
        ) {
            QueueItemDetailsScreen(navController = navController)
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = Screen.EditProfileScreen.route) {
            EditProfileScreen(navController = navController)
        }
        composable(
            route = Screen.FullSizeAvatarScreen.route,
            arguments = listOf(
                navArgument(PICTURE_URL_ARGUMENT_KEY) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profilePictureUrl = backStackEntry.arguments?.getString(PICTURE_URL_ARGUMENT_KEY)

            FullSizeAvatarScreen(
                navController = navController,
                profilePictureUrl = profilePictureUrl
            )
        }
    }
}