package com.onthewake.onthewakelive.feature_auth.data.remote.response

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class AuthResponse(
    val firstName: String,
    val userId: String,
    val token: String
)