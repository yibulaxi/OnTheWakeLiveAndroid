package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import androidx.annotation.Keep

@Keep
@kotlinx.serialization.Serializable
data class RegisterData(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val password: String
)