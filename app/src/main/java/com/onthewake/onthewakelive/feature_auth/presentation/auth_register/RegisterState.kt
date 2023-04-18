package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class RegisterState(
    val isLoading: Boolean = false,
    val firstName: String = "",
    val firstNameError: UIText? = null,
    val lastName: String = "",
    val lastNameError: UIText? = null,
    val phoneNumber: String = "",
    val phoneNumberError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null
)