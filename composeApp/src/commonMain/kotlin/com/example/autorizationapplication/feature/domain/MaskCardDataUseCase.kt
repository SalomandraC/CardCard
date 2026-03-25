package com.example.autorizationapplication.feature.domain

class MaskCardDataUseCase {
    fun maskCardNumber(cardNumber: String, isMaskingEnabled: Boolean): String {
        if (!isMaskingEnabled) return cardNumber

        val cleanedNumber = cardNumber.replace(" ", "")
        if (cleanedNumber.length != 16) return cardNumber

        val firstSix = cleanedNumber.take(6)
        val lastFour = cleanedNumber.takeLast(4)
        val masked = "•••• •••• •••• $lastFour"

        return masked
    }

    fun maskCardHolderName(name: String, isMaskingEnabled: Boolean): String {
        if (!isMaskingEnabled || name.isEmpty()) return name

        val words = name.split(" ")
        val maskedWords = words.map { word ->
            if (word.length <= 2) {
                word
            } else {
                word[0] + "*".repeat(word.length - 2) + word.last()
            }
        }

        return maskedWords.joinToString(" ")
    }

    fun maskCvv(cvv: String, isMaskingEnabled: Boolean): String {
        if (!isMaskingEnabled) return cvv
        return if (cvv.isNotEmpty()) "•••" else ""
    }
}