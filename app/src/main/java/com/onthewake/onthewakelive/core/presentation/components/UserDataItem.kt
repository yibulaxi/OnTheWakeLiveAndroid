package com.onthewake.onthewakelive.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthewake.onthewakelive.R

@Composable
fun UserDataItem(
    title: String,
    subtitle: String,
    showDivider: Boolean = true
) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle.ifEmpty { stringResource(id = R.string.not_specified) })
        if (showDivider) Divider(modifier = Modifier.padding(vertical = 16.dp))
    }
}