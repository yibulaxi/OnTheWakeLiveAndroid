package com.onthewake.onthewakelive.feature_queue.presentation.queue_list

import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

data class QueueState(
    val queue: List<QueueItem> = emptyList(),
    var isQueueLoading: Boolean = false,
    var error: UIText? = null
)