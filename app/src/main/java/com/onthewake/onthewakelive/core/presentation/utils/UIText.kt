package com.onthewake.onthewakelive.core.presentation.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.onthewake.onthewakelive.R

sealed interface UIText {
    data class DynamicString(val value: String?) : UIText
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UIText

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value ?: stringResource(id = R.string.unknown_error)
        is StringResource -> stringResource(resId, *args)
    }

    fun asString(context: Context): String = when (this) {
        is DynamicString -> value ?: context.getString(R.string.unknown_error)
        is StringResource -> context.getString(resId, *args)
    }
}