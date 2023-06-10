package com.onthewake.onthewakelive.feature_profile.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.AnimatedScaffold
import com.onthewake.onthewakelive.core.presentation.components.StandardImageView
import com.onthewake.onthewakelive.core.presentation.components.UserDataItem
import com.onthewake.onthewakelive.core.utils.addPlusPrefix
import com.onthewake.onthewakelive.core.utils.openInstagramProfile
import com.onthewake.onthewakelive.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val snackBarHostState = remember { SnackbarHostState() }
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackBarHostState.showSnackbar(message = error.asString(context))
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getProfile()
    }

    AnimatedScaffold(
        isLoading = state.isLoading,
        snackBarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.profile)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(Screen.QueueScreen.route) { inclusive = true }
                                viewModel.logout()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = stringResource(id = R.string.arrow_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = surfaceColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp, vertical = 22.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StandardImageView(
                        model = state.profilePictureUri,
                        onUserAvatarClicked = { pictureUrl ->
                            navController.navigate(
                                Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                            )
                        }
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = state.firstName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = state.lastName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(bottom = 12.dp),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            navController.navigate(Screen.EditProfileScreen.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UserDataItem(
                        title = stringResource(id = R.string.instagram),
                        subtitle = state.instagram,
                        showDivider = false
                    )
                    if (state.instagram.isNotEmpty()) IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            context.openInstagramProfile(state.instagram)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = stringResource(id = R.string.arrow_forward)
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                UserDataItem(
                    title = stringResource(id = R.string.telegram),
                    subtitle = state.telegram
                )
                UserDataItem(
                    title = stringResource(id = R.string.phone_number),
                    subtitle = state.phoneNumber.addPlusPrefix()
                )
                UserDataItem(
                    title = stringResource(id = R.string.date_of_birth),
                    subtitle = state.dateOfBirth,
                    showDivider = false
                )
            }
        }
    }
}
