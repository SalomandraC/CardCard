package com.example.autorizationapplication.feature.domain

import com.example.autorizationapplication.model.domain.CardType

class DetectCardTypeUseCase {
    operator fun invoke(cardNumber: String): CardType {
        return when {
            cardNumber.startsWith("4") -> CardType.VISA
            cardNumber.startsWith("5") && cardNumber.take(2)
                .toIntOrNull() in 51..55 -> CardType.MASTERCARD

            cardNumber.startsWith("220") -> CardType.MIR
            else -> CardType.UNKNOWN
        }
    }
}