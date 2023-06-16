package com.onthewake.onthewakelive.feature_queue.presentation.add_user_to_the_queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_profile.domain.module.Profile
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.QueueViewModel
import com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components.QueueItemContent
import com.onthewake.onthewakelive.navigation.Screen

@Composable
fun AddUserToTheQueueScreen(
    viewModel: QueueViewModel,
    navController: NavHostController
) {
    val state = viewModel.state.value

    var line by remember { mutableStateOf(Line.RIGHT) }
    var selectedUser by remember { mutableStateOf<Profile?>(null) }
    var firstName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<UIText?>(null) }

    val rightButtonColor = if (line == Line.RIGHT) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val leftButtonColor = if (line == Line.LEFT) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.add_to_queue),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { line = Line.LEFT },
                colors = ButtonDefaults.buttonColors(
                    containerColor = leftButtonColor,
                    contentColor = rightButtonColor
                )
            ) {
                if (line == Line.LEFT) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
                Text(text = stringResource(id = R.string.left))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { line = Line.RIGHT },
                colors = ButtonDefaults.buttonColors(
                    containerColor = rightButtonColor,
                    contentColor = leftButtonColor
                )
            ) {
                if (line == Line.RIGHT) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                }
                Text(text = stringResource(id = R.string.right))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        selectedUser?.let { user ->
            QueueItemContent(
                profilePictureUri = user.profilePictureUri,
                firstName = user.firstName,
                lastName = user.lastName,
                onUserAvatarClicked = {
                    navController.navigate(Screen.FullSizeAvatarScreen.passPictureUrl(it))
                }
            )
            TextButton(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .align(Alignment.End),
                onClick = {
                    selectedUser = null
                    firstName = ""
                }
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        } ?: Column {
            StandardTextField(
                value = firstName,
                onValueChange = {
                    viewModel.searchUser(it)
                    firstName = it
                },
                label = stringResource(id = R.string.first_name),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                errorText = errorMessage
            )

            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.searchedUsers) { searchedUser ->
                    QueueItemContent(
                        profilePictureUri = searchedUser.profilePictureUri,
                        firstName = searchedUser.firstName,
                        lastName = searchedUser.lastName,
                        onUserAvatarClicked = {
                            navController.navigate(Screen.FullSizeAvatarScreen.passPictureUrl(it))
                        },
                        onItemClicked = { selectedUser = searchedUser }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val addToQueueResult = ValidationUseCase().validateAdminAddToQueue(
                    firstName = firstName,
                    queue = state.queue
                )
                if (addToQueueResult.successful) {
                    viewModel.joinTheQueue(
                        line = line, firstName = firstName
                    )
                    navController.popBackStack()
                } else {
                    errorMessage = addToQueueResult.errorMessage
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            enabled = selectedUser != null
        ) {
            Text(text = stringResource(id = R.string.add))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}