package com.example.autorizationapplication.feature

import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.repository.CardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BankCardFormViewModelTest {

    @Test
    fun updateCardNumber_formatsAndDetectsCardType() {
        val repo = CardRepository()
        val viewModel = BankCardFormViewModel(cardRepository = repo)

        viewModel.updateCardNumber("4242-4242-4242-4242")

        assertEquals("4242 4242 4242 4242", viewModel.state.value.cardNumber)
        assertEquals(com.example.autorizationapplication.model.domain.CardType.VISA, viewModel.state.value.cardType)
        assertTrue(viewModel.state.value.saveResultMessage == null)
    }

    @Test
    fun updateCardHolderName_uppercases() {
        val viewModel = BankCardFormViewModel(cardRepository = CardRepository())

        viewModel.updateCardHolderName("john doe")

        assertEquals("JOHN DOE", viewModel.state.value.cardHolderName)
        assertTrue(viewModel.state.value.showCardHolderNameError == false)
    }

    @Test
    fun updateExpiryDate_formatsToMmYy() {
        val viewModel = BankCardFormViewModel(cardRepository = CardRepository())

        viewModel.updateExpiryDate("12/34")

        assertEquals("12/34", viewModel.state.value.expiryDate)
        assertTrue(viewModel.state.value.showExpiryDateError == false)
    }

    @Test
    fun updateCvv_trimsTo3Chars() {
        val viewModel = BankCardFormViewModel(cardRepository = CardRepository())

        viewModel.updateCvv("1234")

        assertEquals("123", viewModel.state.value.cvv)
        assertTrue(viewModel.state.value.showCvvError == false)
    }

    @Test
    fun toggleMasking_flipsFlag() {
        val viewModel = BankCardFormViewModel(cardRepository = CardRepository())

        val initial = viewModel.state.value.isMaskingEnabled
        viewModel.toggleMasking()

        assertTrue(viewModel.state.value.isMaskingEnabled != initial)
    }

    @Test
    fun saveCard_validCard_savesAndSetsSuccessMessage() = runBlocking {
        val repo = CardRepository()
        val viewModel = BankCardFormViewModel(cardRepository = repo)

        // Use a far-future date so the test doesn't depend on the current system time.
        val expiry = "12/99"
        viewModel.updateCardNumber("4242424242424242")
        viewModel.updateCardHolderName("JOHN DOE")
        viewModel.updateExpiryDate(expiry)
        viewModel.updateCvv("123")

        viewModel.saveCard()

        val ok = waitUntil(2_000) {
            viewModel.state.value.saveResultMessage == "Card is valid"
        }
        assertTrue(ok)

        val savedCards = repo.getCards()
        assertTrue(savedCards.isNotEmpty())
        assertEquals("4242424242424242", savedCards.single().cardNumber)
        assertEquals("JOHN DOE", savedCards.single().cardHolderName)
        assertEquals(expiry, savedCards.single().expiryDate)
        assertEquals("123", savedCards.single().cvv)
    }

    @Test
    fun saveCard_invalidCard_setsValidationErrors() = runBlocking {
        val repo = CardRepository()
        val viewModel = BankCardFormViewModel(cardRepository = repo)

        // All fields empty => should fail with Empty* errors.
        viewModel.updateCardNumber("")
        viewModel.updateCardHolderName("")
        viewModel.updateExpiryDate("")
        viewModel.updateCvv("")

        viewModel.saveCard()

        val ok = waitUntil(2_000) {
            val s = viewModel.state.value
            s.showCardNumberError &&
                s.showCardHolderNameError &&
                s.showExpiryDateError &&
                s.showCvvError &&
                s.cardNumberValidation is CardValidationResult.Invalid &&
                s.saveResultMessage == null
        }
        assertTrue(ok)

        assertEquals(null, viewModel.state.value.saveResultMessage)
        assertTrue(viewModel.state.value.cardNumberValidation is CardValidationResult.Invalid)
    }

    private suspend fun waitUntil(timeoutMillis: Long, condition: () -> Boolean): Boolean {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMillis) {
            if (condition()) return true
            delay(10)
        }
        return condition()
    }

}

