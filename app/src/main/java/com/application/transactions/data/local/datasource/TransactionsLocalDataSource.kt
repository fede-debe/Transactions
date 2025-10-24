package com.application.transactions.data.local.datasource

import androidx.room.withTransaction
import com.application.transactions.data.local.dao.TransactionDao
import com.application.transactions.data.local.db.AppDatabase
import com.application.transactions.data.local.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction layer for local transaction persistence.
 * Enables swapping Room with another local mechanism (like an in-memory cache) easily.
 */
interface TransactionsLocalDataSource {
    fun getTransactions(userId: Int): Flow<List<TransactionEntity>>
    suspend fun saveTransactions(userId: Int, items: List<TransactionEntity>)
    suspend fun clearTransactions(userId: Int)
}

/**
 * Room-based implementation of [TransactionsLocalDataSource].
 */
class TransactionsLocalDataSourceImpl(
    private val db: AppDatabase,
    private val dao: TransactionDao
) : TransactionsLocalDataSource {

    override fun getTransactions(userId: Int): Flow<List<TransactionEntity>> =
        dao.getTransactions(userId)

    override suspend fun saveTransactions(userId: Int, items: List<TransactionEntity>) {
        db.withTransaction {
            dao.deleteByUser(userId)
            dao.insertAll(items)
        }
    }

    override suspend fun clearTransactions(userId: Int) {
        dao.deleteByUser(userId)
    }
}