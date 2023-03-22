package com.onthewake.onthewakelive.feature_queue.presentation.queue_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.onthewake.onthewakelive.core.presentation.components.StandardImageView
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.utils.openInstagramProfile
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QueueDetailsScreen(
    imageLoader: ImageLoader,
    navController: NavHostController,
    viewModel: QueueDetailsViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackBarHostState = remember { SnackbarHostState() }

    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    val bgColor = MaterialTheme.colorScheme.background
    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setNavigationBarColor(
            color = bgColor, darkIcons = !darkTheme
        )
        systemUiController.setStatusBarColor(
            color = surfaceColor, darkIcons = !darkTheme
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackBarHostState.showSnackbar(message = message.asString(context))
        }
    }

    AnimatedContent(targetState = state.isLoading) { isLoading ->
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                if (!isLoading) CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.details),
                            fontSize = 28.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceColor,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.QueueScreen.route) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.arrow_back)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            if (isLoading) StandardLoadingView()
            else Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column {
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
                                .padding(top = 20.dp, bottom = 40.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StandardImageView(
                                    imageLoader = imageLoader,
                                    model = state.profilePictureUri,
                                    onUserAvatarClicked = { pictureUrl ->
                                        navController.navigate(
                                            Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
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
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Trick List",
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            IconButton(onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                                navController.navigate(
//                                    Screen.TrickListScreen.passUserId(userId = state.userId)
//                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = stringResource(id = R.string.right_arrow)
                                )
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.instagram),
                                    fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = state.instagram.ifEmpty {
                                    stringResource(id = R.string.not_specified)
                                })
                            }
                            if (state.instagram.isNotEmpty()) {
                                IconButton(
                                    onClick = { context.openInstagramProfile(state.instagram) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = stringResource(id = R.string.right_arrow)
                                    )
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 20.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.telegram),
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = state.telegram.ifEmpty {
                                stringResource(id = R.string.not_specified)
                            })
                        }
                        Divider(modifier = Modifier.padding(vertical = 20.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.phone_number),
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = state.phoneNumber)
                        }
                        Divider(modifier = Modifier.padding(vertical = 20.dp))
                        FormattedDateOfBirth(state.dateOfBirth)
                    }
                }
            }
        }
    }
}