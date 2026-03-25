package com.example.autorizationapplication.feature

import com.example.autorizationapplication.feature.domain.*
import com.example.autorizationapplication.model.domain.*
import com.example.autorizationapplication.model.repository.CardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BankCardFormViewModel(
    private val cardRepository: CardRepository = CardRepository(),
    private val validateCardNumberUseCase: ValidateCardNumberUseCase = ValidateCardNumberUseCase(),
    private val validateExpiryDateUseCase: ValidateExpiryDateUseCase = ValidateExpiryDateUseCase(),
    private val validateCardHolderNameUseCase: ValidateCardHolderNameUseCase = ValidateCardHolderNameUseCase(),
    private val validateCvvUseCase: ValidateCvvUseCase = ValidateCvvUseCase(),
    private val detectCardTypeUseCase: DetectCardTypeUseCase = DetectCardTypeUseCase(),
    private val formatCardNumberUseCase: FormatCardNumberUseCase = FormatCardNumberUseCase(),
    private val maskCardDataUseCase: MaskCardDataUseCase = MaskCardDataUseCase()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(BankCardFormState())
    val state: StateFlow<BankCardFormState> = _state.asStateFlow()

    fun updateCardNumber(cardNumber: String) {
        val formattedNumber = formatCardNumberUseCase(cardNumber)
        val cleanedNumber = formattedNumber.replace(" ", "") // ✅ Один пробел, не два!
        val cardType = if (cleanedNumber.length >= 2) {
            detectCardTypeUseCase(cleanedNumber)
        } else CardType.UNKNOWN

        _state.update {
            it.copy(
                cardNumber = formattedNumber,
                cardType = cardType,
                showCardNumberError = false,
                saveResultMessage = null
            )
        }
        detectBankInfo(cleanedNumber)
    }

    fun updateCardHolderName(name: String) {
        val uppercasedName = name.uppercase()
        _state.update {
            it.copy(
                cardHolderName = uppercasedName,
                showCardHolderNameError = false,
                saveResultMessage = null
            )
        }
    }

    fun updateExpiryDate(expiryDate: String) {
        val digitsOnly = expiryDate.replace(Regex("[^0-9]"), "").take(4) // ✅ Без пробела в Regex
        val formattedDate = if (digitsOnly.length >= 3) {
            "${digitsOnly.take(2)}/${digitsOnly.drop(2)}"
        } else digitsOnly

        _state.update {
            it.copy(
                expiryDate = formattedDate,
                showExpiryDateError = false,
                saveResultMessage = null
            )
        }
    }

    fun updateCvv(cvv: String) {
        val cleanedCvv = cvv.take(3)
        _state.update {
            it.copy(
                cvv = cleanedCvv,
                showCvvError = false,
                saveResultMessage = null
            )
        }
    }

    fun toggleMasking() {
        _state.update { it.copy(isMaskingEnabled = !it.isMaskingEnabled) }
    }

    fun saveCard() {
        viewModelScope.launch {
            val currentState = _state.value
            _state.update {
                it.copy(
                    showCardNumberError = true,
                    showCardHolderNameError = true,
                    showExpiryDateError = true,
                    showCvvError = true
                )
            }

            val cleanedCardNumber = currentState.cardNumber.replace(" ", "")
            val cardNumberValidation = when {
                cleanedCardNumber.isEmpty() -> CardValidationResult.Invalid(listOf(ValidationError.EmptyCardNumber))
                cleanedCardNumber.length != 16 -> CardValidationResult.Invalid(listOf(ValidationError.InvalidCardNumberLength))
                else -> validateCardNumberUseCase(cleanedCardNumber)
            }
            val nameValidation = validateCardHolderNameUseCase(currentState.cardHolderName)
            val expiryValidation = validateExpiryDateUseCase(currentState.expiryDate) // ✅ Используем инжектированный
            val cvvValidation = validateCvvUseCase(currentState.cvv)

            val isFormValid = cardNumberValidation.isValid() &&
                    nameValidation.isValid() &&
                    expiryValidation.isValid() &&
                    cvvValidation.isValid()

            _state.update {
                it.copy(
                    cardNumberValidation = cardNumberValidation,
                    cardHolderNameValidation = nameValidation,
                    expiryDateValidation = expiryValidation,
                    cvvValidation = cvvValidation,
                    isFormValid = isFormValid,
                    saveResultMessage = if (isFormValid) "Card is valid" else null
                )
            }

            if (isFormValid) {
                cardRepository.saveCard(currentState.toBankCard())
                resetFormWithSuccess()
            }
        }
    }

    fun getMaskedCardNumber(): String =
        maskCardDataUseCase.maskCardNumber(_state.value.cardNumber, _state.value.isMaskingEnabled)

    fun getMaskedCardHolderName(): String =
        maskCardDataUseCase.maskCardHolderName(_state.value.cardHolderName, _state.value.isMaskingEnabled)

    fun getMaskedCvv(): String =
        maskCardDataUseCase.maskCvv(_state.value.cvv, _state.value.isMaskingEnabled)

    private fun detectBankInfo(cardNumber: String) {
        if (cardNumber.length >= 6) {
            val bin = cardNumber.take(6)
            val bankName = when {
                bin.startsWith("4") -> "Visa Bank"
                bin.startsWith("51") || bin.startsWith("52") || bin.startsWith("53") ||
                        bin.startsWith("54") || bin.startsWith("55") -> "Mastercard Bank"
                bin.startsWith("220") -> "Mir Bank"
                else -> null
            }
            _state.update { it.copy(bankInfo = bankName) }
        } else {
            _state.update { it.copy(bankInfo = null) }
        }
    }

    private fun resetFormWithSuccess() {
        _state.update { BankCardFormState(saveResultMessage = "Card is valid") }
    }
}