package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class LoginState(
    val isLoading: Boolean = false,
    val signInPhoneNumber: String = "",
    val signInPhoneNumberError: UIText? = null,
    val signInPassword: String = "",
    val signInPasswordError: UIText? = null
)