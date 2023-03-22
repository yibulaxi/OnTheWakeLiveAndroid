package com.onthewake.onthewakelive.feature_queue.domain.module

import kotlinx.serialization.Serializable

@Serializable
data class QueueResponse(
    val isDeleteAction: Boolean = false,
    val queueItem: QueueItem
)