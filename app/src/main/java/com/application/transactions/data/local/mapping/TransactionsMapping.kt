package com.application.transactions.data.local.mapping

import com.application.transactions.data.local.model.TransactionDto
import com.application.transactions.data.local.model.TransactionEntity
import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.domain.MonetaryValue
import com.application.transactions.domain.Transaction
import com.application.transactions.domain.TransactionState
import java.math.BigDecimal
import java.time.LocalDate

fun TransactionDto.toDomain(): Transaction {
    val date = LocalDate.parse(creationTime.substring(0, 10))
    val indicator = when (creditDebitIndicator) {
        "CRDT" -> CreditDebitIndicator.CRDT
        "DBIT" -> CreditDebitIndicator.DBIT
        else -> error("Unknown creditDebitIndicator: $creditDebitIndicator")
    }
    val st = when (state) {
        "PENDING" -> TransactionState.PENDING
        "COMPLETED" -> TransactionState.COMPLETED
        else -> error("Unknown state: $state")
    }
    return Transaction(
        id = id,
        description = description,
        transaction = MonetaryValue(
            amount = BigDecimal(transactionAmountCurrency.amount),
            currencyCode = transactionAmountCurrency.currencyCode
        ),
        creditDebitIndicator = indicator,
        creationDate = date,
        state = st,
    )
}

fun TransactionDto.toEntity(userId: Int): TransactionEntity {
    val date = LocalDate.parse(creationTime.substring(0, 10))
    return TransactionEntity(
        id = id,
        userId = userId,
        description = description,
        amount = BigDecimal(transactionAmountCurrency.amount),
        currencyCode = transactionAmountCurrency.currencyCode,
        creditDebitIndicator = creditDebitIndicator,
        state = state,
        creationDate = date
    )
}

fun TransactionEntity.toDomain(): Transaction {
    val indicator = when (creditDebitIndicator) {
        "CRDT" -> CreditDebitIndicator.CRDT
        "DBIT" -> CreditDebitIndicator.DBIT
        else -> error("Unknown creditDebitIndicator: $creditDebitIndicator")
    }
    val st = when (state) {
        "PENDING" -> TransactionState.PENDING
        "COMPLETED" -> TransactionState.COMPLETED
        else -> error("Unknown state: $state")
    }
    return Transaction(
        id = id,
        description = description,
        transaction = MonetaryValue(
            amount = amount,
            currencyCode = currencyCode
        ),
        creditDebitIndicator = indicator,
        creationDate = creationDate,
        state = st,
    )
}
