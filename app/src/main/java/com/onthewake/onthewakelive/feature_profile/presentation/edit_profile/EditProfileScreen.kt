package com.onthewake.onthewakelive.feature_profile.presentation.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardLoadingView
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.utils.CropActivityResultContract
import com.onthewake.onthewakelive.core.utils.UserProfileSerializer.defaultValue
import com.onthewake.onthewakelive.di.AppModule.dataStore
import com.onthewake.onthewakelive.feature_profile.presentation.edit_profile.EditProfileUiEvent.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    imageLoader: ImageLoader,
    navController: NavHostController
) {
    val state = viewModel.state
    val snackBarHostState = remember { SnackbarHostState() }
    val isImageLoading = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val profilePictureUri = viewModel.selectedProfilePictureUri.value
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    val bgColor = MaterialTheme.colorScheme.background

    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = surfaceColor, darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = bgColor, darkIcons = !darkTheme
        )
    }

    val dataStore by context.dataStore.data
        .collectAsState(initial = defaultValue)

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
        contract = CropActivityResultContract(16f, 16f)
    ) { viewModel.onEvent(CropImage(it)) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { if (it != null) cropActivityLauncher.launch(it) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    AnimatedContent(targetState = state.isLoading) { isLoading ->

        if (isLoading) StandardLoadingView()
        else Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(id = R.string.edit_profile)) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = surfaceColor,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.arrow_back)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(top = 30.dp)
                                    .size(140.dp),
                                shape = RoundedCornerShape(40.dp),
                                onClick = {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
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
                                    if (!isImageLoading.value) {
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            imageVector = Icons.Default.Person,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            contentDescription = stringResource(
                                                id = R.string.person_icon
                                            )
                                        )
                                    }
                                    if (isImageLoading.value) CircularProgressIndicator(
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Image(
                                        modifier = Modifier.fillMaxSize(),
                                        painter = rememberAsyncImagePainter(
                                            model = profilePictureUri ?: state.profilePictureUri,
                                            imageLoader = imageLoader,
                                            onLoading = { isImageLoading.value = true },
                                            onError = { isImageLoading.value = false },
                                            onSuccess = { isImageLoading.value = false }
                                        ),
                                        contentDescription = stringResource(id = R.string.user_picture)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            StandardTextField(
                                value = state.firstName,
                                onValueChange = {
                                    viewModel.onEvent(EditProfileFirstNameChanged(it))
                                },
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
                                onValueChange = {
                                    viewModel.onEvent(EditProfileLastNameChanged(it))
                                },
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
                                onValueChange = {
                                    viewModel.onEvent(EditProfilePhoneNumberChanged(it))
                                },
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
                                onValueChange = {
                                    viewModel.onEvent(
                                        EditProfileTelegramChanged(it)
                                    )
                                },
                                label = stringResource(id = R.string.telegram)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StandardTextField(
                                value = state.instagram,
                                onValueChange = {
                                    viewModel.onEvent(EditProfileInstagramChanged(it))
                                },
                                label = stringResource(id = R.string.instagram)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.dateOfBirth,
                                onValueChange = {
                                    if (it.length <= 8) {
                                        viewModel.onEvent(EditProfileDateOfBirthChanged(it))
                                    }
                                },
                                label = {
                                    Text(text = stringResource(id = R.string.date_of_birth_placeholder))
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            state.profileDateOfBirthError?.let { error ->
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = error.asString(),
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            // TODO code refactor
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (dataStore.firstName == state.firstName &&
                                        dataStore.lastName == state.lastName &&
                                        dataStore.phoneNumber == state.phoneNumber &&
                                        viewModel.selectedProfilePictureUri.value == null &&
                                        dataStore.instagram == state.instagram &&
                                        dataStore.telegram == state.telegram &&
                                        dataStore.dateOfBirth == state.dateOfBirth
                                    ) {
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                context.getString(R.string.nothing_to_update)
                                            )
                                        }
                                    } else {
                                        viewModel.onEvent(EditProfile)
                                        focusManager.clearFocus()
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(text = stringResource(id = R.string.edit))
                            }
                        }
                    }
                }
            }
        }
    }
}