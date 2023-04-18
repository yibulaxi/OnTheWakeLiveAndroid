package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.AnimatedScaffold
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.components.StandardTopBar
import com.onthewake.onthewakelive.core.utils.addPlusPrefix
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.authResult.collectLatest { result ->
            when (result) {
                AuthResult.Authorized -> navController.navigate(Screen.QueueScreen.route) {
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                }
                AuthResult.IncorrectOtp -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.invalid_token_error)
                )
                else -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.unknown_error)
                )
            }
        }
    }

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
    }

    AnimatedScaffold(
        isLoading = state.isLoading,
        topBar = { StandardTopBar(onBackClicked = navController::popBackStack) },
        snackBarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.verify_phone_number),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.code_has_been_sent_to) +
                            state.signUpPhoneNumber.addPlusPrefix(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                StandardTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = state.otp,
                    onValueChange = {
                        if (it.length <= 6) viewModel.onEvent(OtpEvent.OtpChanged(it))
                        if (it.length == 6) {
                            focusManager.clearFocus()
                            viewModel.onEvent(OtpEvent.VerifyOtpAndSignUp)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(R.string.code),
                    errorText = state.otpError
                )
                Spacer(modifier = Modifier.height(4.dp))
                Timer(onResendClicked = { viewModel.resendCode(context) })
            }
        }
    }
}

@Composable
fun Timer(onResendClicked: () -> Unit) {

    var currentTime by remember { mutableStateOf(60L) }

    LaunchedEffect(key1 = currentTime) {
        if (currentTime > 0) {
            delay(1000L)
            currentTime -= 1L
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.didnt_receive_code),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.weight(1f),
            text = currentTime.toString() + stringResource(R.string.seconds),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        TextButton(onClick = onResendClicked, enabled = currentTime <= 0) {
            Text(text = stringResource(R.string.resend))
        }
    }
}