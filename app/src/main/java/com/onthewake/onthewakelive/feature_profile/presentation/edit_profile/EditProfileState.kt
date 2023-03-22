package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class EditProfileState(
    val isLoading: Boolean = false,
    val firstName: String = "",
    val profileFirsNameError: UIText? = null,
    val lastName: String = "",
    val profileLastNameError: UIText? = null,
    val phoneNumber: String = "",
    val profilePhoneNumberError: UIText? = null,
    val telegram: String = "",
    val instagram: String = "",
    val profilePictureUri: String = "",
    val dateOfBirth: String = "",
    val profileDateOfBirthError: UIText? = null
)