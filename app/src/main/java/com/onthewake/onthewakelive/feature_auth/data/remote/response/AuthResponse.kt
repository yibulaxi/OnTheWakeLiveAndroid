package com.onthewake.onthewakelive.feature_auth.data.remote.response

@kotlinx.serialization.Serializable
data class AuthResponse(
    val firstName: String,
    val userId: String,
    val token: String
)