package com.example.autorizationapplication.feature.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatCardNumberUseCaseTest {

    private val useCase = FormatCardNumberUseCase()

    @Test
    fun formats16DigitsInto4DigitGroups() {
        val result = useCase.invoke("4242424242424242")
        assertEquals("4242 4242 4242 4242", result)
    }

    @Test
    fun removesNonDigitsAndFormats() {
        val result = useCase.invoke("4242-4242-4242-4242")
        assertEquals("4242 4242 4242 4242", result)
    }

    @Test
    fun formatsLessThan16DigitsWithoutExtraSpaces() {
        val result = useCase.invoke("1234")
        assertEquals("1234", result)
    }
}

