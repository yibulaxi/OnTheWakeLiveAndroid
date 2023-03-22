package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

import com.onthewake.onthewakelive.feature_auth.data.remote.request.CreateAccountRequest

sealed class OtpEvent {
    data class OtpChanged(val value: String) : OtpEvent()
    data class SignUp(val createAccountRequest: CreateAccountRequest) : OtpEvent()
}