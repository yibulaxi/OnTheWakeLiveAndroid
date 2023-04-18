package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

sealed interface LoginEvent {
    data class PhoneNumberChange(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    object SignIn : LoginEvent
}