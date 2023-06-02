package com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.utils.Constants
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueItem(
    queueItem: QueueItem,
    userId: String?,
    onDetailsClicked: (String) -> Unit,
    onSwipeToDelete: (String) -> Unit,
    onUserAvatarClicked: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val isAdminQueueItem = queueItem.userId in Constants.ADMIN_IDS
    val isOwnQueueItem = queueItem.userId == userId
    val isUserAdmin = userId in Constants.ADMIN_IDS

    val currentItem by rememberUpdatedState(queueItem)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            onSwipeToDelete(currentItem.id)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            false
        }
    )


    if (isOwnQueueItem || isUserAdmin) {
        SwipeToDismiss(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { if (!isAdminQueueItem) onDetailsClicked(queueItem.id) },
            state = dismissState,
            dismissContent = {
                if (isAdminQueueItem) {
                    QueueItemAddedByAdmin(firstName = queueItem.firstName)
                } else {
                    QueueItemContent(
                        queueItem = queueItem,
                        onDetailsClicked = onDetailsClicked,
                        onUserAvatarClicked = onUserAvatarClicked
                    )
                }
            },
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error)
                        .padding(start = 26.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            },
            directions = setOf(DismissDirection.StartToEnd)
        )
    } else if (isAdminQueueItem) {
        QueueItemAddedByAdmin(firstName = queueItem.firstName)
    } else {
        QueueItemContent(
            queueItem = queueItem,
            onDetailsClicked = onDetailsClicked,
            onUserAvatarClicked = onUserAvatarClicked
        )
    }
}