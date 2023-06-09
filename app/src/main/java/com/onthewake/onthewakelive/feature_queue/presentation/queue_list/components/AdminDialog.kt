package com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.window.Dialog
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.components.StandardTextField
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_auth.domain.use_case.ValidationUseCase
import com.onthewake.onthewakelive.feature_queue.domain.module.Line
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

@ExperimentalMaterial3Api
@Composable
fun AdminDialog(
    onDismissRequest: () -> Unit,
    onAddClicked: (Line, String) -> Unit,
    queue: List<QueueItem>
) {
    var line by remember { mutableStateOf(Line.RIGHT) }
    var firstNameFieldState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<UIText?>(null) }

    val rightButtonColor = if (line == Line.RIGHT) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val leftButtonColor = if (line == Line.LEFT) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.primaryContainer

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(330.dp)
                    .padding(14.dp),
                verticalArrangement = Arrangement.Center
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
                        Text(text = stringResource(id = R.string.left_line))
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
                        Text(text = stringResource(id = R.string.right_line))
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
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        val addToQueueResult = ValidationUseCase().validateAdminAddToQueue(
                            firstName = firstNameFieldState, queue = queue
                        )
                        if (addToQueueResult.successful) {
                            onAddClicked(line, firstNameFieldState)
                            onDismissRequest()
                        } else {
                            errorMessage = addToQueueResult.errorMessage
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