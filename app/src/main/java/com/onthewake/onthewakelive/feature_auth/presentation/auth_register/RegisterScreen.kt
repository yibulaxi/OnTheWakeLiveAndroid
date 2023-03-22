package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.*
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(true) {
        viewModel.authResult.collectLatest { result ->
            when (result) {
                AuthResult.OtpTooManyRequests -> snackBarHostState.showSnackbar(
                    message = "Too many request on trying to send OTP"
                )
                AuthResult.OtpInvalidCredentials -> snackBarHostState.showSnackbar(
                    message = "Invalid phone number format"
                )
                AuthResult.UserAlreadyExist -> snackBarHostState.showSnackbar(
                    message = "Oops! User with this phone number already exists"
                )
                AuthResult.UnknownError -> snackBarHostState.showSnackbar(
                    message = "Unknown error"
                )
                else -> Unit
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigateUpEvent.collectLatest { registerData ->
            val registerDataJson = Json.encodeToString(registerData)
            navController.navigate(Screen.OtpScreen.passRegisterData(registerDataJson))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        AnimatedContent(targetState = state.isLoading) { isLoading ->
            if (isLoading) StandardLoadingView()
            else Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StandardTextField(
                        value = state.signUpFirstName,
                        onValueChange = {
                            viewModel.onEvent(SignUpFirstNameChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        label = stringResource(id = R.string.first_name),
                        errorText = state.signUpFirstNameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StandardTextField(
                        value = state.signUpLastName,
                        onValueChange = {
                            viewModel.onEvent(SignUpLastNameChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        label = stringResource(id = R.string.last_name),
                        errorText = state.signUpLastNameError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StandardTextField(
                        value = state.signUpPhoneNumber,
                        onValueChange = {
                            viewModel.onEvent(SignUpPhoneNumberChanged(it))
                        },
                        label = stringResource(id = R.string.phone_number),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        errorText = state.signUpPhoneNumberError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StandardTextField(
                        value = state.signUpPassword,
                        onValueChange = {
                            viewModel.onEvent(SignUpPasswordChanged(it))
                        },
                        label = stringResource(id = R.string.password),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.onEvent(SendOtp(context = context))
                                focusManager.clearFocus()
                            }
                        ),
                        isPasswordTextField = true,
                        errorText = state.signUpPasswordError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(SendOtp(context = context))
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = stringResource(id = R.string.create_account))
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(vertical = 12.dp)
                        .clickable { navController.navigate(Screen.LoginScreen.route) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.already_have_an_account),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.sign_in) + "!",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}