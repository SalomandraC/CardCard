package com.example.autorizationapplication.model.repository

import com.example.autorizationapplication.model.domain.BankCard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardRepository {
    private val _savedCards = MutableStateFlow<List<BankCard>>(emptyList())
    val savedCards: StateFlow<List<BankCard>> = _savedCards.asStateFlow()

    suspend fun saveCard(card: BankCard) {
        _savedCards.value += card
    }

    suspend fun getCards(): List<BankCard> = _savedCards.value
}