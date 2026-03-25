package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateCardHolderNameUseCaseTest {

    private val useCase = ValidateCardHolderNameUseCase()

    @Test
    fun emptyName_returnsEmptyError() {
        val result = useCase.invoke("")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.EmptyCardHolderName), invalid.validationErrors)
    }

    @Test
    fun tooShortName_returnsLengthError() {
        val result = useCase.invoke("A")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidCardHolderNameLength), invalid.validationErrors)
    }

    @Test
    fun invalidCharacters_returnsCharactersError() {
        val result = useCase.invoke("JOHN123")
        assertTrue(result is CardValidationResult.Invalid)

        val invalid = result as CardValidationResult.Invalid
        assertEquals(listOf(ValidationError.InvalidCardHolderNameCharacters), invalid.validationErrors)
    }

    @Test
    fun validName_returnsValid() {
        val result = useCase.invoke("JOHN DOE")
        assertEquals(CardValidationResult.Valid, result)
    }
}

