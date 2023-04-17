package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.AnimatedScaffold
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.utils.SetSystemBarsColor
import com.onthewake.onthewakelive.core.utils.CropActivityResultContract
import com.onthewake.onthewakelive.feature_profile.presentation.edit_profile.EditProfileUiEvent.*
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state.value
    val profilePictureUri = viewModel.selectedProfilePictureUri.value

    val snackBarHostState = remember { SnackbarHostState() }
    val isImageLoading = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    SetSystemBarsColor(statusBarColor = surfaceColor, navigationBarColor = backgroundColor)

    LaunchedEffect(key1 = true) {
        viewModel.snackBarEvent.collectLatest { message ->
            snackBarHostState.showSnackbar(message = message.asString(context))
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigateUp.collectLatest {
            navController.popBackStack()
        }
    }

    val cropActivityLauncher = rememberLauncherForActivityResult(
        contract = CropActivityResultContract()
    ) { viewModel.onEvent(CropImage(it)) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { if (it != null) cropActivityLauncher.launch(it) }

    val calendarState = rememberUseCaseState()
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            boundary = LocalDate.now().minusYears(60L)
                    ..LocalDate.now().minusYears(6L)
        ),
        selection = CalendarSelection.Date(
            selectedDate = LocalDate.now().minusYears(18L)
        ) { date ->
            viewModel.onEvent(DateOfBirthChanged(date.format(formatter)))
        }
    )

    AnimatedScaffold(
        isLoading = state.isLoading,
        snackBarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.edit_profile)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = surfaceColor
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.arrow_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(backgroundColor)
                .padding(horizontal = 24.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(140.dp),
                shape = CircleShape,
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isImageLoading.value) Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.Person,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = stringResource(id = R.string.person_icon)
                    )
                    if (isImageLoading.value) CircularProgressIndicator(
                        modifier = Modifier.size(42.dp)
                    )
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = profilePictureUri ?: state.profilePictureUri,
                        contentDescription = stringResource(id = R.string.user_picture)
                    )
//                    Image(
//                        modifier = Modifier.fillMaxSize(),
//                        painter = rememberAsyncImagePainter(
//                            model = profilePictureUri ?: state.profilePictureUri,
//                            imageLoader = imageLoader,
//                            onLoading = { isImageLoading.value = true },
//                            onError = { isImageLoading.value = false },
//                            onSuccess = { isImageLoading.value = false }
//                        ),
//                        contentDescription = stringResource(id = R.string.user_picture)
//                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            StandardTextField(
                value = state.firstName,
                onValueChange = { viewModel.onEvent(FirstNameChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                label = stringResource(id = R.string.first_name),
                errorText = state.profileFirsNameError
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
                errorText = state.profileLastNameError
            )
            Spacer(modifier = Modifier.height(16.dp))
            StandardTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.onEvent(PhoneNumberChanged(it)) },
                label = stringResource(id = R.string.phone_number),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                errorText = state.profilePhoneNumberError
            )
            Spacer(modifier = Modifier.height(16.dp))
            StandardTextField(
                value = state.telegram,
                onValueChange = { viewModel.onEvent(TelegramChanged(it)) },
                label = stringResource(id = R.string.telegram)
            )
            Spacer(modifier = Modifier.height(16.dp))
            StandardTextField(
                value = state.instagram,
                onValueChange = { viewModel.onEvent(InstagramChanged(it)) },
                label = stringResource(id = R.string.instagram)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { calendarState.show() },
                value = state.dateOfBirth,
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.date_of_birth)) },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onEvent(EditProfile)
                    focusManager.clearFocus()
                }
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
