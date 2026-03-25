package com.example.autorizationapplication.feature.domain

class FormatCardNumberUseCase {
    operator fun invoke(cardNumber: String): String {
        val cleaned = cardNumber.replace(Regex("[^0-9]"), "")
        val chunks = cleaned.chunked(4)
        return chunks.joinToString(" ")
    }
}