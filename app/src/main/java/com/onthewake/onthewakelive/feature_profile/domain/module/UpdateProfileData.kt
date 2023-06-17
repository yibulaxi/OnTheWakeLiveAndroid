package com.onthewake.onthewakelive.feature_profile.domain.module

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateProfileData(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val instagram: String,
    val telegram: String,
    val dateOfBirth: String,
    val profilePictureUri: String?
)
