package com.onthewake.onthewakelive.feature_splash.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.SetSystemBarsColor

@Composable
fun ServerUnavailableScreen() {
    SetSystemBarsColor(systemBarsColor = MaterialTheme.colorScheme.background)

    val compositionResult: LottieCompositionResult =
        rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.server_error))

    val progress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        iterations = LottieConstants.IterateForever,
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimation(
            composition = compositionResult.value,
            progress = { progress },
            modifier = Modifier.size(240.dp)
        )
        Text(
            text = stringResource(R.string.server_unavailable_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
            text = stringResource(R.string.server_unavailable_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}