package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.feature_auth.data.remote.request.AuthRequest
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val validationUseCase: ValidationUseCase
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SignInPhoneNumberChanged -> _state.value = state.value.copy(
                signInPhoneNumber = event.value
            )
            is LoginEvent.SignInPasswordChanged -> _state.value = state.value.copy(
                signInPassword = event.value
            )
            is LoginEvent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        val phoneNumberResult = validationUseCase.validatePhoneNumber(state.value.signInPhoneNumber)
        val passwordResult = validationUseCase.validatePassword(state.value.signInPassword)

        val hasError = listOf(phoneNumberResult, passwordResult).any { !it.successful }

        if (hasError) {
            _state.value = state.value.copy(
                signInPhoneNumberError = phoneNumberResult.errorMessage,
                signInPasswordError = passwordResult.errorMessage
            )
            return
        }

        _state.value = state.value.copy(
            signInPhoneNumberError = null,
            signInPasswordError = null
        )

        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.signIn(
                AuthRequest(
                    phoneNumber = state.value.signInPhoneNumber.trim(),
                    password = state.value.signInPassword.trim()
                )
            )
            _authResult.emit(result)
            _state.value = state.value.copy(isLoading = false)
        }
    }
}
