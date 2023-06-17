package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

sealed interface RegisterEvent {
    data class FirstNameChanged(val value: String) : RegisterEvent
    data class LastNameChanged(val value: String) : RegisterEvent
    data class PhoneNumberChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    object SignUp : RegisterEvent
}