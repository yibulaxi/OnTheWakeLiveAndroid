package com.onthewake.onthewakelive.feature_auth.data.remote.response

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class SignUpResponse(
    val userId: String,
)