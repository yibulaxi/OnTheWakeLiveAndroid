package com.onthewake.onthewakelive.core.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.core.utils.Constants.ADMIN_IDS
import com.onthewake.onthewakelive.core.utils.Constants.INSTAGRAM_URL
import retrofit2.HttpException
import java.io.IOException

// SharedPreferences Utils
fun SharedPreferences.put(key: String, value: String?) {
    this.edit().putString(key, value).apply()
}

fun Context.openInstagramProfile(instagram: String) = this.startActivity(
    Intent(Intent.ACTION_VIEW, Uri.parse("$INSTAGRAM_URL/$instagram/"))
)

fun Context.openNotificationSettings() {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(
            "android.provider.extra.APP_PACKAGE", this@openNotificationSettings.packageName
        )
        this@openNotificationSettings.startActivity(this)
    }
}

// Networking Utils
fun handleNetworkError(exception: Exception): UIText = when (exception) {
    is HttpException -> when (exception.code()) {
        in 400..499 -> UIText.StringResource(R.string.user_error)
        500 -> UIText.StringResource(R.string.server_error)
        else -> UIText.StringResource(R.string.unknown_error)
    }

    is IOException -> UIText.StringResource(R.string.server_error)
    else -> UIText.StringResource(R.string.unknown_error)
}

fun String?.isUserAdmin(): Boolean = this in ADMIN_IDS

fun String.addPlusPrefix(): String = "+$this"