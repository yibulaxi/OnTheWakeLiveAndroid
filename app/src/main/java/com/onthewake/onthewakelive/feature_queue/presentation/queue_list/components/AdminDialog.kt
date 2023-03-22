package com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

@ExperimentalMaterial3Api
@Composable
fun AdminDialog(
    showDialog: (Boolean) -> Unit,
    onAddClicked: (Boolean, String) -> Unit,
    queue: List<QueueItem>
) {
    var isLeftButtonActive by remember { mutableStateOf(false) }
    var isRightButtonActive by remember { mutableStateOf(true) }

    var firstNameFieldState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<UIText?>(null) }

    val rightButtonColor = if (isRightButtonActive) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer
    val leftButtonColor = if (isLeftButtonActive) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer

    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.add_to_queue),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            isLeftButtonActive = !isLeftButtonActive
                            isRightButtonActive = !isRightButtonActive
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = leftButtonColor,
                            contentColor = rightButtonColor
                        )
                    ) {
                        if (isLeftButtonActive) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        }
                        Text(text = stringResource(id = R.string.left_line_admin))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            isLeftButtonActive = !isLeftButtonActive
                            isRightButtonActive = !isRightButtonActive
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = rightButtonColor,
                            contentColor = leftButtonColor
                        ),
                    ) {
                        if (isRightButtonActive) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        }
                        Text(text = stringResource(id = R.string.right_line_admin))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                StandardTextField(
                    value = firstNameFieldState,
                    onValueChange = { firstNameFieldState = it },
                    label = stringResource(id = R.string.first_name),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    errorText = errorMessage
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val addToQueueResult = ValidationUseCase().validateAdminAddToQueue(
                            firstName = firstNameFieldState, queue = queue
                        )
                        errorMessage = addToQueueResult.errorMessage

                        if (addToQueueResult.successful) {
                            onAddClicked(isLeftButtonActive, firstNameFieldState)
                            showDialog(false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    enabled = firstNameFieldState.isNotEmpty()
                ) {
                    Text(text = stringResource(id = R.string.add))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}