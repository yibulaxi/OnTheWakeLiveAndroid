package com.onthewake.onthewakelive.feature_queue.domain.module

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class QueueItem(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUri: String,
    val isLeftQueue: Boolean,
    val timestamp: Long
)