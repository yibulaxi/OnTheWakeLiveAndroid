package com.onthewake.onthewakelive.feature_auth.presentation.auth_register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.onthewake.onthewakelive.core.presentation.components.AnimatedScaffold
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.FirstNameChanged
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.LastNameChanged
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.PasswordChanged
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.PhoneNumberChanged
import com.onthewake.onthewakelive.feature_auth.presentation.auth_register.RegisterEvent.SendOtp
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

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
        viewModel.authResult.collectLatest { authResult ->
            when (authResult) {
                AuthResult.OtpTooManyRequests -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.otp_too_many_requests)
                )
                AuthResult.OtpInvalidCredentials -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.invalid_phone_number)
                )
                AuthResult.UserAlreadyExist -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.user_already_exists)
                )
                AuthResult.UnknownError -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.unknown_error)
                )
                else -> Unit
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { error ->
            snackBarHostState.showSnackbar(message = error.asString(context))
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigateUpEvent.collectLatest { registerData ->
            navController.navigate(Screen.OtpScreen.passRegisterData(registerData))
        }
    }

    AnimatedScaffold(
        isLoading = state.isLoading,
        snackBarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                StandardTextField(
                    value = state.firstName,
                    onValueChange = { viewModel.onEvent(FirstNameChanged(it)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(id = R.string.first_name),
                    errorText = state.firstNameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                StandardTextField(
                    value = state.lastName,
                    onValueChange = { viewModel.onEvent(LastNameChanged(it)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(id = R.string.last_name),
                    errorText = state.lastNameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                StandardTextField(
                    value = state.phoneNumber,
                    onValueChange = { viewModel.onEvent(PhoneNumberChanged(it)) },
                    label = stringResource(id = R.string.phone_number),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    errorText = state.phoneNumberError,
                    isPhoneNumberTextField = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                StandardTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(PasswordChanged(it)) },
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
                    errorText = state.passwordError
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