package com.application.transactions.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: Int,
    val description: String,
    val amount: BigDecimal,
    val currencyCode: String,
    val creditDebitIndicator: String,
    val state: String,
    val creationDate: LocalDate
)