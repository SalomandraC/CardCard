package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError

class ValidateCvvUseCase {
    operator fun invoke(cvv: String): CardValidationResult {
        return when {
            cvv.isEmpty() -> CardValidationResult.Invalid(listOf(ValidationError.EmptyCvv))
            cvv.length != 3 -> CardValidationResult.Invalid(listOf(ValidationError.InvalidCvvLength))
            !cvv.matches(Regex("^[0-9]+$")) -> CardValidationResult.Invalid(listOf(ValidationError.InvalidCvvCharacters))
            else -> CardValidationResult.Valid
        }
    }
}