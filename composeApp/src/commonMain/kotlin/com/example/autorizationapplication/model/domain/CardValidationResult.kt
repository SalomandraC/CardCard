package com.example.autorizationapplication.model.domain

sealed class CardValidationResult {
    object Valid : CardValidationResult()
    data class Invalid(val validationErrors: List<ValidationError>) : CardValidationResult()

    fun isValid(): Boolean = this is Valid

    fun getErrors(): List<ValidationError> = when (this) {
        is Valid -> emptyList()
        is Invalid -> validationErrors
    }
}

sealed class ValidationError {
    object EmptyCardNumber : ValidationError()
    object InvalidCardNumberLength : ValidationError()
    object InvalidLuhnChecksum : ValidationError()

    object EmptyCardHolderName : ValidationError()
    object InvalidCardHolderNameLength : ValidationError()
    object InvalidCardHolderNameCharacters : ValidationError()

    object EmptyExpiryDate : ValidationError()
    object InvalidExpiryDateFormat : ValidationError()
    object ExpiredCard : ValidationError()

    object EmptyCvv : ValidationError()
    object InvalidCvvLength : ValidationError()
    object InvalidCvvCharacters : ValidationError()
}