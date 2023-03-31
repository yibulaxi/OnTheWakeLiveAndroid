package com.onthewake.onthewakelive.feature_profile.domain.module

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class ProfileState(
    val isLoading: Boolean = false,
    val error: UIText? = null,
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUri: String = "",
    val phoneNumber: String = "",
    val telegram: String = "",
    val instagram: String = "",
    val dateOfBirth: String = ""
)