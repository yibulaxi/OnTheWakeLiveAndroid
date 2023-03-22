package com.onthewake.onthewakelive.feature_queue.domain.module

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class AddToQueueResult(
    val successful: Boolean = false,
    val error: UIText? = null
)