package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

sealed interface LoginEvent {
    data class SignInPhoneNumberChanged(val value: String) : LoginEvent
    data class SignInPasswordChanged(val value: String) : LoginEvent
    object SignIn : LoginEvent
}