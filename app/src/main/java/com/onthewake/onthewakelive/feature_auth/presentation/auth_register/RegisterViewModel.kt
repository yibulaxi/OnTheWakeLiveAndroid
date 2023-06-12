package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Resource
import com.onthewake.onthewakelive.core.utils.addPlusPrefix
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    private val _snackBarEvent = MutableSharedFlow<UIText>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    private val _navigateUpEvent = MutableSharedFlow<String>()
    val navigateUpEvent = _navigateUpEvent.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FirstNameChanged -> _state.value = state.value.copy(
                firstName = event.value
            )

            is RegisterEvent.LastNameChanged -> _state.value = state.value.copy(
                lastName = event.value
            )

            is RegisterEvent.PhoneNumberChanged -> if (event.value.isDigitsOnly()) {
                _state.value = state.value.copy(phoneNumber = event.value)
            }

            is RegisterEvent.PasswordChanged -> _state.value = state.value.copy(
                password = event.value
            )

            is RegisterEvent.SendOtp -> sendOtp(context = event.context)
        }
    }

    private fun sendOtp(context: Context) {
        val firstName = state.value.firstName.trim()
        val lastName = state.value.lastName.trim()
        val phoneNumber = state.value.phoneNumber.trim()
        val password = state.value.password.trim()

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
                firstNameError = firstNameResult.errorMessage,
                lastNameError = lastNameResult.errorMessage,
                phoneNumberError = phoneNumberResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
            return
        }
        _state.value = state.value.copy(
            firstNameError = null,
            lastNameError = null,
            phoneNumberError = null,
            passwordError = null
        )

        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            when (val isUserAlreadyExists = repository.isUserAlreadyExists(phoneNumber)) {
                is Resource.Success -> {
                    if (isUserAlreadyExists.data == true) {
                        _authResult.emit(AuthResult.UserAlreadyExist)
                        _state.value = state.value.copy(isLoading = false)
                        return@launch
                    }
                }
                is Resource.Error -> _snackBarEvent.emit(isUserAlreadyExists.message)
            }

            val result = repository.sendOtp(
                phoneNumber = phoneNumber.addPlusPrefix(),
                isResendAction = false,
                context = context
            )
            val registerData = Json.encodeToString(
                RegisterData(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    password = password
                )
            )
            if (result == AuthResult.OtpSentSuccess) _navigateUpEvent.emit(registerData)
            _authResult.emit(result)
            _state.value = state.value.copy(isLoading = false)
        }
    }
}