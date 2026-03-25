package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Suppress("DEPRECATION")
class ValidateExpiryDateUseCase {

    operator fun invoke(expiryDate: String): CardValidationResult {
        return when {
            expiryDate.isEmpty() ->
                CardValidationResult.Invalid(listOf(ValidationError.EmptyExpiryDate))

            !expiryDate.matches(Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")) ->
                CardValidationResult.Invalid(listOf(ValidationError.InvalidExpiryDateFormat))

            isExpired(expiryDate) ->
                CardValidationResult.Invalid(listOf(ValidationError.ExpiredCard))

            else -> CardValidationResult.Valid
        }
    }

    private fun isExpired(expiryDate: String): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val currentYear = now.year % 100
        val currentMonth = now.monthNumber

        val (monthStr, yearStr) = expiryDate.split("/")
        val expiryMonth = monthStr.toInt()
        val expiryYear = yearStr.toInt()

        return expiryYear < currentYear ||
                (expiryYear == currentYear && expiryMonth < currentMonth)
    }
}