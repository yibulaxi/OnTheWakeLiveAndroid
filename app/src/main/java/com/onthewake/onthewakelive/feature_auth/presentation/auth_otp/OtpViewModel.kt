package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthewake.onthewakelive.core.utils.Constants.REGISTER_DATA_ARGUMENT_KEY
import com.onthewake.onthewakelive.feature_auth.data.remote.request.CreateAccountRequest
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.domain.repository.AuthRepository
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(OtpState())
    val state: State<OtpState> = _state

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    init {
        savedStateHandle.get<String>(REGISTER_DATA_ARGUMENT_KEY)?.let { registerDataJson ->
            val registerData = Json.decodeFromString<RegisterData>(registerDataJson)
            _state.value = state.value.copy(
                signUpFirstName = registerData.firstName,
                signUpLastName = registerData.lastName,
                signUpPhoneNumber = registerData.phoneNumber,
                signUpPassword = registerData.password
            )
        }
    }

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.OtpChanged -> _state.value = state.value.copy(otp = event.value)
            is OtpEvent.VerifyOtpAndSignUp -> verifyOtpAndSignUp()
        }
    }

    private fun verifyOtpAndSignUp() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val otpResult = repository.verifyOtp(state.value.otp)

            if (otpResult == AuthResult.IncorrectOtp) {
                _authResult.emit(AuthResult.IncorrectOtp)
                _state.value = state.value.copy(isLoading = false)
                return@launch
            }

            val signUpResult = repository.signUp(
                CreateAccountRequest(
                    firstName = state.value.signUpFirstName,
                    lastName = state.value.signUpLastName,
                    phoneNumber = state.value.signUpPhoneNumber,
                    password = state.value.signUpPassword
                )
            )
            _authResult.emit(signUpResult)
            _state.value = state.value.copy(isLoading = false)
        }
    }

    fun resendCode(context: Context) {
        viewModelScope.launch {
            val resendResult = repository.sendOtp(
                phoneNumber = state.value.signUpPhoneNumber,
                isResendAction = true,
                context = context
            )
            _authResult.emit(resendResult)
        }
    }
}