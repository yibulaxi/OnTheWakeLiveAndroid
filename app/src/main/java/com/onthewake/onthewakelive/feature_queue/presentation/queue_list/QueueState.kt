package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

data class QueueState(
    val userId: String? = null,
    val queue: List<QueueItem> = emptyList(),
    val isQueueLoading: Boolean = false,
    val showLeaveQueueDialog: Boolean = false,
    val queueItemIdToDelete: String? = null,
    val showAdminDialog: Boolean = false,
    val error: UIText? = null
)