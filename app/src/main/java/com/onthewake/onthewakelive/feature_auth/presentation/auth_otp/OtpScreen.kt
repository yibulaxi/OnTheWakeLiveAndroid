package com.onthewake.onthewakelive.feature_auth.presentation.auth_otp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.components.StandardTopBar
import com.onthewake.onthewakelive.feature_auth.domain.models.AuthResult
import com.onthewake.onthewakelive.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.authResult.collectLatest { result ->
            when (result) {
                AuthResult.Authorized -> navController.navigate(Screen.QueueScreen.route) {
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                }
                AuthResult.IncorrectOtp -> snackBarHostState.showSnackbar(
                    message = "Incorrect OTP"
                )
                else -> snackBarHostState.showSnackbar(
                    message = context.getString(R.string.unknown_error)
                )
            }
        }
    }

    AnimatedContent(targetState = state.isLoading) { isLoading ->
        if (isLoading) StandardLoadingView()
        else Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = { StandardTopBar(onBackClicked = { navController.popBackStack() }) }
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
                        text = "An 6 digit code has been sent to\n${state.signUpPhoneNumber}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StandardTextField(
                        value = state.otp,
                        onValueChange = {
                            if (it.length <= 6) viewModel.onEvent(OtpEvent.OtpChanged(it))
                            if (it.length == 6) {
                                focusManager.clearFocus()
                                viewModel.verifyOtpAndSignUp()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        label = "Code",
                        errorText = state.otpError
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Timer(onResendClicked = { viewModel.resendCode(context) })
                }
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
            text = "Didn't receive code? Resend after ",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "${currentTime}s",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        TextButton(onClick = onResendClicked, enabled = currentTime <= 0) {
            Text(text = "Resend")
        }
    }
}