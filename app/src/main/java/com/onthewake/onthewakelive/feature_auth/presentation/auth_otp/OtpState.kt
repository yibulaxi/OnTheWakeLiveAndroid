package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult

data class OtpState(
    val isLoading: Boolean = false,
    val signUpResult: AuthResult? = null,
    val signUpFirstName: String = "",
    val signUpLastName: String = "",
    val signUpPhoneNumber: String = "",
    val signUpPassword: String = "",
    val otp: String = "",
    val otpError: UIText? = null
)