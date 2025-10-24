package com.application.transactions.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionAmountCurrencyDto(
    val amount: String,
    val currencyCode: String
)

@Serializable
data class TransactionDto(
    val id: String,
    val description: String,
    val transactionAmountCurrency: TransactionAmountCurrencyDto,
    val creditDebitIndicator: String,
    val creationTime: String,
    val state: String,
)