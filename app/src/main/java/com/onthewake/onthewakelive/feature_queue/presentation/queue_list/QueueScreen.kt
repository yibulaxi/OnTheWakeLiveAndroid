package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import coil.ImageLoader
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.AnimatedShimmer
import com.onthewake.onthewakelive.core.presentation.utils.SetSystemBarsColor
import com.onthewake.onthewakelive.core.utils.isUserAdmin
import com.onthewake.onthewakelive.core.utils.openNotificationSettings
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.*
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun QueueScreen(
    viewModel: QueueViewModel = hiltViewModel(),
    navController: NavHostController,
    imageLoader: ImageLoader
) {
    val state = viewModel.state.value
    val userId = viewModel.userId

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var queueItemIdToDelete by remember { mutableStateOf("") }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val pagerState = rememberPagerState(pageCount = 2, initialPage = 1)
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    SetSystemBarsColor(systemBarsColor = surfaceColor)

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->

            hasNotificationPermission = isGranted

            if (!isGranted) scope.launch {
                val result = snackBarHostState.showSnackbar(
                    "Разрешите показ уведомений",
                    actionLabel = "Settings"
                )
                if (result == SnackbarResult.ActionPerformed)
                    context.openNotificationSettings()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackBarHostState.showSnackbar(message = message.asString(context))
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.connectToQueue()
            else if (event == Lifecycle.Event.ON_PAUSE) viewModel.disconnect()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showConfirmationDialog) LeaveQueueConfirmationDialog(
        showDialog = { showConfirmationDialog = it },
        isUserAdmin = viewModel.userId.isUserAdmin(),
        onLeaveQueue = { viewModel.deleteQueueItem(queueItemIdToDelete) }
    )

    if (viewModel.showDialog) AdminDialog(
        showDialog = { viewModel.showDialog = it },
        queue = state.queue,
        onAddClicked = { isLeftQueue, firstName ->
            viewModel.addToQueue(
                isLeftQueue = isLeftQueue,
                firstName = firstName
            )
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.queue),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!state.isQueueLoading) FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                    if (hasNotificationPermission) {
                        if (userId.isUserAdmin()) viewModel.showDialog = true
                        else viewModel.addToQueue(isLeftQueue = pagerState.currentPage == 0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_icon),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabLayout(pagerState = pagerState)

            AnimatedContent(targetState = state.isQueueLoading) { isLoading ->
                if (isLoading) AnimatedShimmer()
                else HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> QueueLeftContent(
                            state = state,
                            userId = userId,
                            imageLoader = imageLoader,
                            onDetailsClicked = { queueItemId ->
                                navController.navigate(
                                    Screen.QueueDetailsScreen.passItemId(itemId = queueItemId)
                                )
                            },
                            onSwipeToDelete = { queueItemId ->
                                showConfirmationDialog = true
                                queueItemIdToDelete = queueItemId
                            },
                            onUserAvatarClicked = { pictureUrl ->
                                navController.navigate(
                                    Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                                )
                            }
                        )
                        1 -> QueueRightContent(
                            state = state,
                            userId = userId,
                            imageLoader = imageLoader,
                            onDetailsClicked = { queueItemId ->
                                navController.navigate(
                                    Screen.QueueDetailsScreen.passItemId(itemId = queueItemId)
                                )
                            },
                            onSwipeToDelete = { queueItemId ->
                                showConfirmationDialog = true
                                queueItemIdToDelete = queueItemId
                            },
                            onUserAvatarClicked = { pictureUrl ->
                                navController.navigate(
                                    Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun QueueLeftContent(
    state: QueueState,
    userId: String?,
    imageLoader: ImageLoader,
    onDetailsClicked: (String) -> Unit,
    onSwipeToDelete: (String) -> Unit,
    onUserAvatarClicked: (String) -> Unit
) {
    val leftQueue = remember(state.queue) {
        state.queue.filter { it.isLeftQueue }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = leftQueue.isEmpty()) { isLeftQueueEmpty ->
            if (isLeftQueueEmpty) EmptyContent(modifier = Modifier.align(Alignment.Center))
            else LazyColumn(
                contentPadding = PaddingValues(10.dp),
                reverseLayout = true
            ) {
                items(leftQueue) { item ->
                    QueueItem(
                        queueItem = item,
                        imageLoader = imageLoader,
                        userId = userId,
                        onDetailsClicked = onDetailsClicked,
                        onSwipeToDelete = onSwipeToDelete,
                        onUserAvatarClicked = onUserAvatarClicked
                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun QueueRightContent(
    state: QueueState,
    userId: String?,
    imageLoader: ImageLoader,
    onDetailsClicked: (String) -> Unit,
    onSwipeToDelete: (String) -> Unit,
    onUserAvatarClicked: (String) -> Unit
) {
    val rightQueue = remember(state.queue) {
        state.queue.filter { !it.isLeftQueue }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = rightQueue.isEmpty()) { isRightQueueEmpty ->
            if (isRightQueueEmpty) EmptyContent(modifier = Modifier.align(Alignment.Center))
            else LazyColumn(
                contentPadding = PaddingValues(10.dp),
                reverseLayout = true
            ) {
                items(rightQueue) { item ->
                    QueueItem(
                        queueItem = item,
                        imageLoader = imageLoader,
                        userId = userId,
                        onDetailsClicked = onDetailsClicked,
                        onSwipeToDelete = onSwipeToDelete,
                        onUserAvatarClicked = onUserAvatarClicked
                    )
                }
            }
        }
    }
}