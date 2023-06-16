package com.onthewake.onthewakelive.feature_queue.data.remote.model

import androidx.annotation.Keep
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.Action
import com.onthewake.onthewakelive.feature_queue.domain.module.Line

@Keep
@kotlinx.serialization.Serializable
data class QueueSocketMessage(
    val action: Action,
    val queueItemId: String? = null,
    val user: Profile? = null,
    val line: Line? = null,
    val firstName: String? = null
)
