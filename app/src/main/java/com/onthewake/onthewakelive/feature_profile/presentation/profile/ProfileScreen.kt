package com.onthewake.onthewakelive.feature_profile.presentation.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.ImageLoader
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.FormattedDateOfBirth
import com.onthewake.onthewakelive.core.presentation.components.ProfileItem
import com.onthewake.onthewakelive.core.presentation.components.StandardImageView
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.dataStore
import com.onthewake.onthewakelive.core.utils.UserProfileSerializer.defaultValue
import com.onthewake.onthewakelive.core.utils.openInstagramProfile
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    imageLoader: ImageLoader
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = surfaceColor, darkIcons = !darkTheme
        )
    }

    val dataStore = remember {
        context.dataStore.data
    }.collectAsState(initial = defaultValue)

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackBarHostState.showSnackbar(message = message.asString(context))
        }
    }

    LaunchedEffect(key1 = true) {
        if (dataStore.value.firstName.isEmpty() || dataStore.value.lastName.isEmpty()) {
            viewModel.getProfile()
        }
    }

    AnimatedContent(targetState = viewModel.isLoading.value) { isLoading ->
        if (isLoading) StandardLoadingView()
        else Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.profile))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceColor,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
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
                        .background(color = surfaceColor),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 20.dp, bottom = 40.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 4.dp, top = 2.dp),
                                onClick = {
                                    haptic.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove
                                    )
                                    navController.navigate(Screen.EditProfileScreen.route)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(id = R.string.edit_icon)
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StandardImageView(
                                    imageLoader = imageLoader,
                                    model = dataStore.value.profilePictureUri,
                                    onUserAvatarClicked = { pictureUrl ->
                                        navController.navigate(
                                            Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = dataStore.value.firstName,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = dataStore.value.lastName,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileItem(
                            title = stringResource(id = R.string.instagram),
                            subtitle = dataStore.value.instagram
                        )
                        if (dataStore.value.instagram.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    context.openInstagramProfile(dataStore.value.instagram)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = stringResource(id = R.string.right_arrow)
                                )
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 18.dp))
                    ProfileItem(
                        title = stringResource(id = R.string.telegram),
                        subtitle = dataStore.value.telegram
                    )
                    Divider(modifier = Modifier.padding(vertical = 18.dp))
                    ProfileItem(
                        title = stringResource(id = R.string.phone_number),
                        subtitle = dataStore.value.phoneNumber
                    )
                    Divider(modifier = Modifier.padding(vertical = 18.dp))
                    FormattedDateOfBirth(dataStore.value.dateOfBirth)
                }
            }
        }
    }
}
