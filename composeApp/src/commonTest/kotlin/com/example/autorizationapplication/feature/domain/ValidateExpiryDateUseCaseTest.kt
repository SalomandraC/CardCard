package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateExpiryDateUseCaseTest {

    private val useCase = ValidateExpiryDateUseCase()

    @Test
    fun emptyExpiryDate_returnsEmptyError() {
        val result = useCase.invoke("")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.EmptyExpiryDate), invalid.validationErrors)
    }

    @Test
    fun invalidFormat_returnsFormatError() {
        val result = useCase.invoke("13/99")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidExpiryDateFormat), invalid.validationErrors)
    }

    @Test
    fun validExpiryDate_returnsValid() {
        // Use a far-future date so the test doesn't depend on the current system time.
        val result = useCase.invoke("12/99")
        assertEquals(CardValidationResult.Valid, result)
    }

    @Test
    fun expiredExpiryDate_returnsExpiredError() {
        // Use a far-past date so it must be expired for a long time.
        val expired = "01/00"
        val result = useCase.invoke(expired)
        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.ExpiredCard), invalid.validationErrors)
    }
}

