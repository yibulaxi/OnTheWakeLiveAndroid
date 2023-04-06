package com.onthewake.onthewakelive.feature_profile.domain.module

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Profile(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUri: String,
    val phoneNumber: String,
    val telegram: String,
    val instagram: String,
    val dateOfBirth: String
)
