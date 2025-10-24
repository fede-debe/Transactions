package com.application.transactions.data.remote

interface TransactionsApi {
    suspend fun fetchTransactions(userId: Int): Result<String>
}