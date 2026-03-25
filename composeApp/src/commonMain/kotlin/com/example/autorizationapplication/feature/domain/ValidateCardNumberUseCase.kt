package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError

class ValidateCardNumberUseCase {
    operator fun invoke(cardNumber: String): CardValidationResult {
        return when {
            cardNumber.isEmpty() -> CardValidationResult.Invalid(listOf(ValidationError.EmptyCardNumber))
            cardNumber.length != 16 -> CardValidationResult.Invalid(listOf(ValidationError.InvalidCardNumberLength))
            !isValidLuhn(cardNumber) -> CardValidationResult.Invalid(listOf(ValidationError.InvalidLuhnChecksum))
            else -> CardValidationResult.Valid
        }
    }

    private fun isValidLuhn(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false

        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].digitToInt()

            if (alternate) {
                n *= 2
                if (n > 9) {
                    n -= 9
                }
            }

            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}