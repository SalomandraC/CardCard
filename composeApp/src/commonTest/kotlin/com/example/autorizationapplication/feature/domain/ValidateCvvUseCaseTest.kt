package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateCvvUseCaseTest {

    private val useCase = ValidateCvvUseCase()

    @Test
    fun emptyCvv_returnsEmptyError() {
        val result = useCase.invoke("")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.EmptyCvv), invalid.validationErrors)
    }

    @Test
    fun invalidLength_returnsLengthError() {
        val result = useCase.invoke("12")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidCvvLength), invalid.validationErrors)
    }

    @Test
    fun invalidCharacters_returnsCharactersError() {
        val result = useCase.invoke("12A")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidCvvCharacters), invalid.validationErrors)
    }

    @Test
    fun validCvv_returnsValid() {
        val result = useCase.invoke("123")
        assertEquals(CardValidationResult.Valid, result)
    }
}

