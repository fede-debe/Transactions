package com.application.transactions.domain

import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val id: String,
    val description: String,
    val transaction: MonetaryValue,
    val creditDebitIndicator: CreditDebitIndicator,
    val creationDate: LocalDate,
    val state: TransactionState,
)

enum class CreditDebitIndicator { CRDT, DBIT }
enum class TransactionState { PENDING, COMPLETED }
data class MonetaryValue(
    val amount: BigDecimal,
    val currencyCode: String
)