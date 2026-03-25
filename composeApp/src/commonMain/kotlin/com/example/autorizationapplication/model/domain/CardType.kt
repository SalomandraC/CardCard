package com.example.autorizationapplication.model.domain

enum class CardType {
    VISA,
    MASTERCARD,
    MIR,
    UNKNOWN;

    fun getDisplayName(): String {
        return when (this) {
            VISA -> "Visa"
            MASTERCARD -> "Mastercard"
            MIR -> "Mir"
            UNKNOWN -> "Unknown"
        }
    }
    fun getCardLogo(): String {
        return "💳";
    }
}