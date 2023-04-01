package com.onthewake.onthewakelive.feature_auth.domain.use_case

import com.onthewake.onthewakelive.R
import com.onthewake.onthewakelive.core.presentation.utils.UIText
import com.onthewake.onthewakelive.feature_auth.domain.models.ValidationResult
import com.onthewake.onthewakelive.feature_queue.domain.module.QueueItem

class ValidationUseCase {
    fun validateFirstName(firstName: String): ValidationResult {
        if (firstName.isBlank()) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_first_name_error)
        )
        return ValidationResult(successful = true)
    }

    fun validateLastName(lastName: String): ValidationResult {
        if (lastName.isBlank()) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_last_name_error)
        )
        return ValidationResult(successful = true)
    }

    fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        if (phoneNumber.isBlank()) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_phone_number_error)
        )
        return ValidationResult(successful = true)
    }

    fun validateAdminAddToQueue(firstName: String, queue: List<QueueItem>): ValidationResult {
        val formattedFirstName = firstName.trim().lowercase()
        val isUserAlreadyInQueue = queue.none {
            it.firstName.lowercase() == formattedFirstName
        }
        if (!isUserAlreadyInQueue) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_user_error)
        )
        return ValidationResult(successful = true)
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_password_error)
        )
        if (password.length < 6) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_password_length_error)
        )
        return ValidationResult(successful = true)
    }

    fun validateOtp(otp: String): ValidationResult {
        if (otp.isBlank()) return ValidationResult(
            successful = false,
            errorMessage = UIText.StringResource(R.string.validate_otp_error)
        )
        return ValidationResult(successful = true)
    }
}