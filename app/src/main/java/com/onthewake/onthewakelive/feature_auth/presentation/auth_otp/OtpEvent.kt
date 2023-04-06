package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

sealed interface OtpEvent {
    data class OtpChanged(val value: String) : OtpEvent
    object VerifyOtpAndSignUp : OtpEvent
}