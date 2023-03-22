package com.onthewake.onthewakelive.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop
import java.io.File

class CropActivityResultContract : ActivityResultContract<Uri, Uri?>() {

    override fun createIntent(context: Context, input: Uri): Intent {
        return UCrop.of(
            input, Uri.fromFile(File(context.cacheDir, input.lastPathSegment ?: ""))
        ).withAspectRatio(16f, 16f).getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (intent == null) return null
        return UCrop.getOutput(intent)
    }
}