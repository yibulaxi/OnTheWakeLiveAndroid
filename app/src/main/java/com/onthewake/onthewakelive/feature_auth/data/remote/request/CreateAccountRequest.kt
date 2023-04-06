package com.onthewake.onthewakelive.feature_auth.data.remote.request

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class CreateAccountRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String
)