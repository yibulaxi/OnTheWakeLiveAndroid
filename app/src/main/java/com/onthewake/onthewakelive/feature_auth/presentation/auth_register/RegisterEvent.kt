package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import android.content.Context

sealed interface RegisterEvent {
    data class SignUpFirstNameChanged(val value: String) : RegisterEvent
    data class SignUpLastNameChanged(val value: String) : RegisterEvent
    data class SignUpPhoneNumberChanged(val value: String) : RegisterEvent
    data class SignUpPasswordChanged(val value: String) : RegisterEvent
    data class SendOtp(val context: Context) : RegisterEvent
}