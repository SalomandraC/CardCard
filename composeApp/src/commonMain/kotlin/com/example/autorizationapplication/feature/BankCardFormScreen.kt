package com.example.autorizationapplication.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.autorizationapplication.feature.components.CardInputField
import com.example.autorizationapplication.feature.components.CardVisual
import com.example.autorizationapplication.model.domain.CardValidationResult
import com.example.autorizationapplication.model.domain.ValidationError

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BankCardFormScreen(
    viewModel: BankCardFormViewModel = remember { BankCardFormViewModel() }
) {
    val state by viewModel.state.collectAsState()
    var cardNumberFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var expiryFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(state.cardNumber) {
        if (cardNumberFieldValue.text != state.cardNumber) {
            val cursor = cardNumberFieldValue.selection.start.coerceAtMost(state.cardNumber.length)
            cardNumberFieldValue = TextFieldValue(
                text = state.cardNumber,
                selection = TextRange(cursor)
            )
        }
    }

    LaunchedEffect(state.expiryDate) {
        if (expiryFieldValue.text != state.expiryDate) {
            val cursor = expiryFieldValue.selection.start.coerceAtMost(state.expiryDate.length)
            expiryFieldValue = TextFieldValue(
                text = state.expiryDate,
                selection = TextRange(cursor)
            )
        }
    }

    // ✅ БЕЗ remember — значения вычисляются "на лету" при каждой рекомпозиции
    val maskedCardNumber = viewModel.getMaskedCardNumber()
    val maskedCardHolderName = viewModel.getMaskedCardHolderName()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardVisual(
            cardNumber = maskedCardNumber,
            cardHolderName = maskedCardHolderName,
            expiryDate = state.expiryDate,
            cvv = state.cvv,
            cardType = state.cardType,
            isMaskingEnabled = state.isMaskingEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.toggleMasking() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isMaskingEnabled) "Show Data" else "Hide Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = cardNumberFieldValue,
                onValueChange = { newValue ->
                    val digitsBeforeCursor = newValue.text
                        .take(newValue.selection.start)
                        .count(Char::isDigit)

                    viewModel.updateCardNumber(newValue.text)

                    val formatted = formatCardNumber(newValue.text)
                    val newCursor = cursorPositionByDigits(formatted, digitsBeforeCursor)
                    cardNumberFieldValue = TextFieldValue(
                        text = formatted,
                        selection = TextRange(newCursor)
                    )
                },
                label = { Text("Card Number") },
                placeholder = { Text("1234 5678 9012 3456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.showCardNumberError && state.cardNumberValidation is CardValidationResult.Invalid,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            if (state.showCardNumberError) {
                val errorMessage = getErrorMessage(state.cardNumberValidation)
                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardInputField(
            value = state.cardHolderName,
            onValueChange = viewModel::updateCardHolderName,
            label = "Card Holder Name",
            placeholder = "JOHN DOE",
            keyboardType = KeyboardType.Text,
            isError = state.showCardHolderNameError && state.cardHolderNameValidation is CardValidationResult.Invalid,
            errorMessage = if (state.showCardHolderNameError) getErrorMessage(state.cardHolderNameValidation) else null
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardInputField(
                value = expiryFieldValue.text,
                onValueChange = { newText ->
                    val digitsBefore = expiryFieldValue.text
                        .take(expiryFieldValue.selection.start)
                        .count(Char::isDigit)

                    val formatted = formatExpiry(newText)
                    viewModel.updateExpiryDate(formatted)

                    val newCursor = cursorPositionByDigits(formatted, digitsBefore)
                    expiryFieldValue = TextFieldValue(
                        text = formatted,
                        selection = TextRange(newCursor.coerceAtMost(formatted.length))
                    )
                },
                label = "Expiry Date",
                placeholder = "MM/YY",
                keyboardType = KeyboardType.Number,
                isError = state.showExpiryDateError && state.expiryDateValidation is CardValidationResult.Invalid,
                errorMessage = if (state.showExpiryDateError) getErrorMessage(state.expiryDateValidation) else null,
                modifier = Modifier.weight(1f)
            )

            CardInputField(
                value = state.cvv,
                onValueChange = viewModel::updateCvv,
                label = "CVV/CVC",
                placeholder = "123",
                keyboardType = KeyboardType.Number,
                isError = state.showCvvError && state.cvvValidation is CardValidationResult.Invalid,
                errorMessage = if (state.showCvvError) getErrorMessage(state.cvvValidation) else null,
                modifier = Modifier.weight(1f),
                visualTransformation = if (state.isMaskingEnabled && state.cvv.length == 3) {
                    CvvMaskTransformation()
                } else {
                    VisualTransformation.None
                }
            )
        }

        if (state.bankInfo != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Bank: ${state.bankInfo}",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.saveCard() },
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Card")
        }

        AnimatedVisibility(
            visible = !state.saveResultMessage.isNullOrEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = "✓ ${state.saveResultMessage.orEmpty()}",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private class CvvMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            AnnotatedString("•••"),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = 3
                override fun transformedToOriginal(offset: Int): Int = text.text.length
            }
        )
    }

    override fun equals(other: Any?): Boolean = other is CvvMaskTransformation
    override fun hashCode(): Int = 43
}

private fun getErrorMessage(validationResult: CardValidationResult): String? {
    val errors = validationResult.getErrors()
    return if (errors.isEmpty()) null else {
        errors.joinToString(", ") { error ->
            when (error) {
                ValidationError.EmptyCardNumber -> "Card number is required"
                ValidationError.InvalidCardNumberLength -> "Card number must be 16 digits"
                ValidationError.InvalidLuhnChecksum -> "Invalid card number"
                ValidationError.EmptyCardHolderName -> "Card holder name is required"
                ValidationError.InvalidCardHolderNameLength -> "Name must be at least 2 characters"
                ValidationError.InvalidCardHolderNameCharacters -> "Use only letters and spaces"
                ValidationError.EmptyExpiryDate -> "Expiry date is required"
                ValidationError.InvalidExpiryDateFormat -> "Use format MM/YY"
                ValidationError.ExpiredCard -> "Card has expired"
                ValidationError.EmptyCvv -> "CVV is required"
                ValidationError.InvalidCvvLength -> "CVV must be 3 digits"
                ValidationError.InvalidCvvCharacters -> "Use only numbers"
                else -> "Invalid input"
            }
        }
    }
}

private fun formatCardNumber(rawValue: String): String {
    val cleaned = rawValue.filter { it.isDigit() }.take(16)
    return cleaned.chunked(4).joinToString(" ")
}

private fun formatExpiry(rawValue: String): String {
    val digits = rawValue.filter { it.isDigit() }.take(4)
    return when {
        digits.length >= 3 -> digits.substring(0, 2) + "/" + digits.substring(2)
        else -> digits
    }
}

private fun cursorPositionByDigits(formatted: String, digitsCount: Int): Int {
    if (digitsCount <= 0) return 0

    var digitsSeen = 0
    formatted.forEachIndexed { index, ch ->
        if (ch.isDigit()) {
            digitsSeen++
            if (digitsSeen == digitsCount) return index + 1
        }
    }

    return formatted.length
}