package com.onthewake.onthewakelive.feature_queue.presentation.queue_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthewake.onthewakelive.core.presentation.components.StandardImageView

@Composable
fun QueueItemContent(
    queueItemId: String? = null,
    firstName: String,
    lastName: String,
    profilePictureUri: String,
    onItemClicked: (String) -> Unit = {},
    onUserAvatarClicked: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(queueItemId ?: "") }
            .clip(shape = MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StandardImageView(
            model = profilePictureUri,
            onUserAvatarClicked = onUserAvatarClicked
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = firstName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = lastName,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}