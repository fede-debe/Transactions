package com.application.transactions.domain

import java.math.BigDecimal
import java.time.LocalDate

data class TransactionGroup(
    val date: LocalDate,
    val creditDebitIndicator: CreditDebitIndicator,
    val transactions: List<Transaction>
) {

    init {
        require(transactions.isNotEmpty()) { "TransactionGroup must have at least 1 transaction" }
    }

    private val decimal: BigDecimal =
        transactions.fold(BigDecimal.ZERO) { acc, t -> acc + t.transaction.amount }
    val totalAmount: BigDecimal =
        if (creditDebitIndicator == CreditDebitIndicator.CRDT) decimal else decimal.negate()
    val count = transactions.size
    val descriptions = transactions.map { it.description }
}

sealed class TransactionsSection() {
    open val groups: List<TransactionGroup> = listOf()
    data class Pending(override val groups: List<TransactionGroup>) : TransactionsSection()
    data class Completed(override val groups: List<TransactionGroup>) : TransactionsSection()
}