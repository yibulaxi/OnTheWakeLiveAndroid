package com.onthewake.onthewakelive.feature_queue.presentation.queue_details

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class QueueItemDetailsState(
    val isLoading: Boolean = false,
    val error: UIText? = null,
    val userId: String = "",
    val currentUserId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val profilePictureUri: String = "",
    val telegram: String = "",
    val instagram: String = "",
    val dateOfBirth: String = ""
)