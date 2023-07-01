package com.onthewake.onthewakelive.feature_queue.domain.module

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class QueueSocketResponse(
    val action: Action,
    val queueItem: QueueItem
)

enum class Action {
    JOIN_THE_QUEUE,
    LEAVE_THE_QUEUE,
    ADD_USER_TO_THE_QUEUE
}