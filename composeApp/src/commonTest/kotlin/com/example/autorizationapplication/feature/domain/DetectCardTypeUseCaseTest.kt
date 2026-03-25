package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardType
import kotlin.test.Test
import kotlin.test.assertEquals

class DetectCardTypeUseCaseTest {

    private val useCase = DetectCardTypeUseCase()

    @Test
    fun visa_isDetectedByLeading4() {
        val result = useCase.invoke("4" + "0".repeat(15))
        assertEquals(CardType.VISA, result)
    }

    @Test
    fun mastercard_isDetectedBy51to55() {
        assertEquals(CardType.MASTERCARD, useCase.invoke("51" + "0".repeat(14)))
        assertEquals(CardType.MASTERCARD, useCase.invoke("55" + "0".repeat(14)))
    }

    @Test
    fun mir_isDetectedBy220() {
        val result = useCase.invoke("220" + "0".repeat(13))
        assertEquals(CardType.MIR, result)
    }

    @Test
    fun unknown_forOtherPrefixes() {
        val result = useCase.invoke("300" + "0".repeat(13))
        assertEquals(CardType.UNKNOWN, result)
    }
}

