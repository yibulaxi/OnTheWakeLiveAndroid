package com.onthewake.onthewakelive.navigation

import com.onthewake.onthewakelive.core.utils.Constants.DETAILS_ARGUMENT_KEY
import com.onthewake.onthewakelive.core.utils.Constants.PICTURE_URL_ARGUMENT_KEY
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object ServerUnavailableScreen : Screen("server_unavailable_screen")
    object LoginScreen : Screen("login_screen")
    object RegisterScreen : Screen("register_screen")

    object QueueScreen : Screen("queue_screen")
    object AddUserToTheQueueScreen : Screen("add_user_to_the_queue_screen")
    object QueueDetailsScreen : Screen(
        "queue_details_screen/{$DETAILS_ARGUMENT_KEY}"
    ) {
        fun passItemId(itemId: String): String = "queue_details_screen/$itemId"
    }

    object ProfileScreen : Screen("profile_screen")
    object EditProfileScreen : Screen("edit_profile_screen")
    object FullSizeAvatarScreen : Screen(
        "full_size_avatar_screen/{$PICTURE_URL_ARGUMENT_KEY}"
    ) {
        fun passPictureUrl(url: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "full_size_avatar_screen/$encodedUrl"
        }
    }
}

sealed class NavigationRoute(val route: String) {
    object AuthNavigation : NavigationRoute("auth_navigation")
    object MainNavigation : NavigationRoute("main_navigation")
}