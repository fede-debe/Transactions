package com.application.transactions.domain.repository

import com.application.transactions.data.local.datasource.TransactionsLocalDataSource
import com.application.transactions.data.local.mapping.toDomain
import com.application.transactions.data.local.mapping.toEntity
import com.application.transactions.data.local.model.TransactionDto
import com.application.transactions.data.remote.TransactionsApi
import com.application.transactions.domain.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.collections.map

interface TransactionsRepository {
    /** One-shot fetch from network that also persists locally. */
    suspend fun fetchTransactions(userId: Int): Result<List<Transaction>>

    /** Observe locally cached data (Room/DB). */
    fun observeLocalData(userId: Int): Flow<List<Transaction>>
}


class TransactionsRepositoryImpl @Inject constructor(
    private val remote: TransactionsApi,
    private val local: TransactionsLocalDataSource,
    private val json: Json
) : TransactionsRepository {

    override suspend fun fetchTransactions(userId: Int): Result<List<Transaction>> {
        return remote.fetchTransactions(userId).map { raw ->
            val dtos = decode(raw)
            val entities = dtos.map { it.toEntity(userId) }
            local.saveTransactions(userId, entities)
            entities.map { it.toDomain() }
        }
    }

    override fun observeLocalData(userId: Int): Flow<List<Transaction>> {
        return local.getTransactions(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun decode(rawJson: String): List<TransactionDto> {
        val trimmed = rawJson.trim()
        val isEmptyJson = trimmed.isEmpty() || trimmed == "[]" || trimmed.equals("null", ignoreCase = true)
        if (isEmptyJson) { return emptyList() }

        return try {
            json.decodeFromString<List<TransactionDto>>(trimmed)
        } catch (_: Exception) {
            val envelope = json.decodeFromString<TransactionsEnvelope>(trimmed)
            envelope.transactions
        }
    }

    @Serializable
    private data class TransactionsEnvelope(
        val transactions: List<TransactionDto>
    )
}