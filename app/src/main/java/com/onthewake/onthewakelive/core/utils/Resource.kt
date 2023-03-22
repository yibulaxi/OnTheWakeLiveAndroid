package com.onthewake.onthewakelive.core.utils

import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText

typealias SimpleResource = Resource<Unit>

sealed class Resource<T>(
    val data: T? = null,
    val message: UIText = UIText.StringResource(R.string.unknown_error)
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: UIText, data: T? = null) : Resource<T>(data, message)
}
