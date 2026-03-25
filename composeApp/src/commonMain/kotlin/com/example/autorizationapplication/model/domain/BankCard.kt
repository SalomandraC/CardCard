package com.example.autorizationapplication.model.domain

import kotlinx.serialization.Serializable

@Serializable
data class BankCard(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expiryDate: String = "",
    val cvv: String = ""
) {
    fun isValid(): Boolean {
        return cardNumber.isNotEmpty() &&
                cardHolderName.isNotEmpty() &&
                expiryDate.isNotEmpty() &&
                cvv.isNotEmpty()
    }
}
