package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.AnimatedShimmer
import com.onthewake.onthewakelive.core.utils.isUserAdmin
import com.onthewake.onthewakelive.core.utils.openNotificationSettings
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.AdminDialog
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.EmptyContent
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.LeaveQueueConfirmationDialog
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.QueueItem
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.TabLayout
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QueueScreen(
    viewModel: QueueViewModel,
    navController: NavHostController
) {
    val state = viewModel.state.value

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current

    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

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
        onResult = { isGranted -> hasNotificationPermission = isGranted }
    )

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackBarHostState.showSnackbar(message = message.asString(context))
        }
    }

    if (state.showLeaveQueueDialog) LeaveQueueConfirmationDialog(
        onDismissRequest = viewModel::hideLeaveQueueDialog,
        isUserAdmin = state.userId.isUserAdmin(),
        onLeaveQueue = viewModel::leaveTheQueue
    )

    if (state.showAdminDialog) AdminDialog(
        onDismissRequest = viewModel::toggleAdminDialog,
        queue = state.queue,
        onAddClicked = viewModel::joinTheQueue
    )

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.connectToQueue()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.queue),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        },
        floatingActionButton = {
            if (!state.isQueueLoading) FloatingActionButton(
                modifier = Modifier.padding(
                    bottom = if (state.userId.isUserAdmin()) 0.dp else 80.dp
                ),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    if (hasNotificationPermission) {
                        if (state.userId.isUserAdmin()) viewModel.toggleAdminDialog()
                        else viewModel.joinTheQueue(
                            line = if (pagerState.currentPage == 0) Line.LEFT else Line.RIGHT
                        )
                    } else scope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = context.getString(R.string.allow_notifications),
                            actionLabel = context.getString(R.string.settings),
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            context.openNotificationSettings()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add),
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabLayout(pagerState = pagerState)

            AnimatedContent(targetState = state.isQueueLoading) { isLoading ->
                if (isLoading) AnimatedShimmer()
                else HorizontalPager(state = pagerState, pageSize = PageSize.Fill) { page ->
                    when (page) {
                        0 -> {
                            val leftQueue = remember(state.queue) {
                                state.queue.filter { it.line == Line.LEFT }
                            }
                            QueueContent(
                                queue = leftQueue,
                                userId = state.userId,
                                onDetailsClicked = { queueItemId ->
                                    navController.navigate(
                                        Screen.QueueDetailsScreen.passItemId(itemId = queueItemId)
                                    )
                                },
                                onSwipeToDelete = viewModel::onSwipedToDelete,
                                onUserAvatarClicked = { pictureUrl ->
                                    navController.navigate(
                                        Screen.FullSizeAvatarScreen.passPictureUrl(pictureUrl)
                                    )
                                }
                            )
                        }

                        1 -> {
                            val rightQueue = remember(state.queue) {
                                state.queue.filter { it.line == Line.RIGHT }
                            }
                            QueueContent(
                                queue = rightQueue,
                                userId = state.userId,
                                onDetailsClicked = { queueItemId ->
                                    navController.navigate(
                                        Screen.QueueDetailsScreen.passItemId(itemId = queueItemId)
                                    )
                                },
                                onSwipeToDelete = viewModel::onSwipedToDelete,
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
}

@Composable
private fun QueueContent(
    queue: List<QueueItem>,
    userId: String?,
    onDetailsClicked: (String) -> Unit,
    onSwipeToDelete: (String) -> Unit,
    onUserAvatarClicked: (String) -> Unit
) {
    AnimatedContent(targetState = queue.isEmpty()) { isQueueEmpty ->
        if (isQueueEmpty) EmptyContent()
        else LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(queue) { item ->
                QueueItem(
                    queueItem = item,
                    userId = userId,
                    onDetailsClicked = onDetailsClicked,
                    onSwipeToDelete = onSwipeToDelete,
                    onUserAvatarClicked = onUserAvatarClicked
                )
            }
        }
    }
}