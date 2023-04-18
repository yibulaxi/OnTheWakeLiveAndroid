package com.onthewake.onthewakelive.feature_auth.presentation.auth_login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
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
            is LoginEvent.PhoneNumberChange -> if (event.value.isDigitsOnly()) {
                _state.value = state.value.copy(phoneNumber = event.value)
            }
            is LoginEvent.PasswordChanged -> _state.value = state.value.copy(
                password = event.value
            )
            is LoginEvent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        val phoneNumberResult = validationUseCase.validatePhoneNumber(state.value.phoneNumber)
        val passwordResult = validationUseCase.validatePassword(state.value.password)

        val hasError = listOf(phoneNumberResult, passwordResult).any { !it.successful }

        if (hasError) {
            _state.value = state.value.copy(
                phoneNumberError = phoneNumberResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }

        _state.value = state.value.copy(
            phoneNumberError = null,
            passwordError = null
        )

        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val result = repository.signIn(
                AuthRequest(
                    phoneNumber = state.value.phoneNumber.trim(),
                    password = state.value.password.trim()
                )
            )
            _authResult.emit(result)
            _state.value = state.value.copy(isLoading = false)
        }
    }
}
