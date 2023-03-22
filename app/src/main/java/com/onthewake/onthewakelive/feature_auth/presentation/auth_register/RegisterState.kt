package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class RegisterState(
    val isLoading: Boolean = false,
    val signUpFirstName: String = "",
    val signUpFirstNameError: UIText? = null,
    val signUpLastName: String = "",
    val signUpLastNameError: UIText? = null,
    val signUpPhoneNumber: String = "",
    val signUpPhoneNumberError: UIText? = null,
    val signUpPassword: String = "",
    val signUpPasswordError: UIText? = null
)