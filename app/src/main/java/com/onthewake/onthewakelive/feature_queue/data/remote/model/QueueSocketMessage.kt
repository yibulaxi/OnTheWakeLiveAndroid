package com.onthewake.onthewakelive.feature_queue.data.remote.model

import com.onthewake.onthewakelive.feature_queue.domain.module.Action
import com.onthewake.onthewakelive.feature_queue.domain.module.Line

@kotlinx.serialization.Serializable
data class QueueSocketMessage(
    val action: Action,
    val queueItemId: String? = null,
    val line: Line ? = null,
    val firstName: String? = null
)
