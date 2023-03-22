package com.onthewake.onthewakelive.feature_auth.domain.models

import com.onthewake.onthewakelive.core.presentation.utils.UIText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UIText? = null
)