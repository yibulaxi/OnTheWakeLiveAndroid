package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val validationUseCase: ValidationUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    private val _navigateUpEvent = MutableSharedFlow<RegisterData>()
    val navigateUpEvent = _navigateUpEvent.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SignUpFirstNameChanged -> _state.value = state.value.copy(
                signUpFirstName = event.value
            )
            is RegisterEvent.SignUpLastNameChanged -> _state.value = state.value.copy(
                signUpLastName = event.value
            )
            is RegisterEvent.SignUpPhoneNumberChanged -> _state.value = state.value.copy(
                signUpPhoneNumber = event.value
            )
            is RegisterEvent.SignUpPasswordChanged -> _state.value = state.value.copy(
                signUpPassword = event.value
            )
            is RegisterEvent.SendOtp -> sendOtp(context = event.context)
        }
    }

    private fun sendOtp(context: Context) {
        val firstName = state.value.signUpFirstName.trim()
        val lastName = state.value.signUpLastName.trim()
        val phoneNumber = state.value.signUpPhoneNumber.trim()
        val password = state.value.signUpPassword.trim()

        val firstNameResult = validationUseCase.validateFirstName(firstName)
        val lastNameResult = validationUseCase.validateLastName(lastName)
        val phoneNumberResult = validationUseCase.validatePhoneNumber(phoneNumber)
        val passwordResult = validationUseCase.validatePassword(password)

        val hasError = listOf(
            firstNameResult,
            lastNameResult,
            phoneNumberResult,
            passwordResult
        ).any { !it.successful }

        if (hasError) {
            _state.value = state.value.copy(
                signUpFirstNameError = firstNameResult.errorMessage,
                signUpLastNameError = lastNameResult.errorMessage,
                signUpPhoneNumberError = phoneNumberResult.errorMessage,
                signUpPasswordError = passwordResult.errorMessage
            )
            return
        } else _state.value = state.value.copy(
            signUpFirstNameError = null,
            signUpLastNameError = null,
            signUpPhoneNumberError = null,
            signUpPasswordError = null
        )

        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val isUserAlreadyExists = repository.isUserAlreadyExists(phoneNumber)
            if (isUserAlreadyExists.data == true) {
                _authResult.emit(AuthResult.UserAlreadyExist)
                return@launch
            }

            val result = repository.sendOtp(
                phoneNumber = state.value.signUpPhoneNumber.trim(),
                isResendAction = false,
                context = context
            )
            if (result == AuthResult.OtpSentSuccess) _navigateUpEvent.emit(
                RegisterData(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    password = password
                )
            )
            _authResult.emit(result)

            _state.value = state.value.copy(isLoading = false)
        }
    }
}