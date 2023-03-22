package com.onthewake.onthewakelive.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText

@Composable
fun StandardTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorText: UIText? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions(onDone = {}),
    isPasswordTextField: Boolean = false
) {
    var showPassword by remember { mutableStateOf(false) }

    val visualTransformation = if (!showPassword && isPasswordTextField)
        PasswordVisualTransformation() else VisualTransformation.None

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        isError = errorText != null,
        trailingIcon = {
            if (isPasswordTextField && value.isNotEmpty()) {
                val image = if (showPassword) Icons.Default.VisibilityOff
                else Icons.Default.Visibility

                val description = if (showPassword) stringResource(id = R.string.hide_password)
                else stringResource(id = R.string.show_password)

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        }
    )
    if (errorText != null) Text(
        modifier = Modifier.fillMaxWidth(),
        text = errorText.asString(),
        color = MaterialTheme.colorScheme.error,
        fontSize = 14.sp,
        textAlign = TextAlign.End
    )
}