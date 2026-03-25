package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateCardNumberUseCaseTest {

    private val useCase = ValidateCardNumberUseCase()

    @Test
    fun emptyCardNumber_returnsEmptyError() {
        val result = useCase.invoke("")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.EmptyCardNumber), invalid.validationErrors)
    }

    @Test
    fun invalidLength_returnsLengthError() {
        val result = useCase.invoke("123")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidCardNumberLength), invalid.validationErrors)
    }

    @Test
    fun invalidLuhn_returnsLuhnError() {
        val result = useCase.invoke("4242424242424241")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidLuhnChecksum), invalid.validationErrors)
    }

    @Test
    fun validCardNumber_returnsValid() {
        val result = useCase.invoke("4242424242424242")
        assertEquals(CardValidationResult.Valid, result)
    }
}

