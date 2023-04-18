package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class LoginState(
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val phoneNumberError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null
)