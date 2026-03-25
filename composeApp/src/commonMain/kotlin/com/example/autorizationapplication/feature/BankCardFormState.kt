package com.example.autorizationapplication.feature

import com.example.autorizationapplication.model.domain.BankCard
import com.example.autorizationapplication.model.domain.CardType
import com.example.autorizationapplication.model.domain.CardValidationResult

data class BankCardFormState(
    val cardNumber: String = "", // ✅ Пустая строка, не " "
    val cardHolderName: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val cardType: CardType = CardType.UNKNOWN,
    val isMaskingEnabled: Boolean = false,
    val showCardNumberError: Boolean = false,
    val showCardHolderNameError: Boolean = false,
    val showExpiryDateError: Boolean = false,
    val showCvvError: Boolean = false,
    val cardNumberValidation: CardValidationResult = CardValidationResult.Valid,
    val cardHolderNameValidation: CardValidationResult = CardValidationResult.Valid,
    val expiryDateValidation: CardValidationResult = CardValidationResult.Valid,
    val cvvValidation: CardValidationResult = CardValidationResult.Valid,
    val isFormValid: Boolean = false,
    val bankInfo: String? = null,
    val saveResultMessage: String? = null
) {
    fun toBankCard(): BankCard = BankCard(
        cardNumber = cardNumber.replace(" ", ""), 
        cardHolderName = cardHolderName,
        expiryDate = expiryDate,
        cvv = cvv
    )
}