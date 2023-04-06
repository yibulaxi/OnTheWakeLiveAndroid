package com.onthewake.onthewakelive.core.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.onthewake.onthewakelive.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.onthewake.onthewakelive.core.domain.modules.BottomNavItem
import com.onthewake.onthewakelive.navigation.Screen

@Composable
fun StandardScaffold(
    navController: NavHostController,
    isUserAdmin: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isPortraitOrientation = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val bottomNavItems: List<BottomNavItem> = listOf(
        BottomNavItem(
            route = Screen.QueueScreen.route,
            icon = Icons.Default.Home,
            contentDescription = context.getString(R.string.queue)
        ),
        BottomNavItem(
            route = Screen.ProfileScreen.route,
            icon = Icons.Default.Person,
            contentDescription = context.getString(R.string.profile)
        )
    )

    val shouldShowBottomBar = navBackStackEntry?.destination?.route in listOf(
        Screen.QueueScreen.route, Screen.ProfileScreen.route
    ) && !isUserAdmin

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar && isPortraitOrientation) NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected =
                        navController.currentDestination?.route?.startsWith(item.route) == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.contentDescription
                            )
                        },
                        label = { Text(text = item.contentDescription) },
                        selected = selected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (navController.currentDestination?.route != item.route) {
                                navController.navigate(item.route) { launchSingleTop = true }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}