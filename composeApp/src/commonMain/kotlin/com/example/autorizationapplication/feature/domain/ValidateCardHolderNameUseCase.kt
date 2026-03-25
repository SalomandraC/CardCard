package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError

class ValidateCardHolderNameUseCase {
    operator fun invoke(name: String): CardValidationResult {
        return when {
            name.isEmpty() -> CardValidationResult.Invalid(listOf(ValidationError.EmptyCardHolderName))
            name.length < 2 -> CardValidationResult.Invalid(listOf(ValidationError.InvalidCardHolderNameLength))
            !name.matches(Regex("^[A-Z\\s]+$")) ->
                CardValidationResult.Invalid(listOf(ValidationError.InvalidCardHolderNameCharacters))
            else -> CardValidationResult.Valid
        }
    }
}