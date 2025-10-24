package com.application.transactions.data.remote

import kotlinx.coroutines.delay
import java.io.IOException

class TransactionsApiImpl(
    private val latencyMs: Long = 400L
) : TransactionsApi {

    override suspend fun fetchTransactions(userId: Int): Result<String> {
        delay(latencyMs)

        val payload = when (userId) {
            10000 -> "[]" // explicit empty
            10010 -> """[ ${singlePending("2019-05-15")} ]"""
            10011 -> """{ "transactions": [ ${pendingGrouped()} ] }"""
            10012 -> """[ ${singleCompleted("2019-05-16")} ]"""
            10013 -> """{ "transactions": [ ${completedGrouped()} ] }"""
            10014 -> """{ "transactions": [ ${mixedSingle()} ] }"""
            10015 -> """[ ${mixedGrouped()} ]"""

            // Optional: explicit simulated failures
            90000 -> return Result.failure(IOException("Simulated network error"))
            else  -> return Result.failure(IllegalArgumentException("Unknown userId: $userId"))
        }

        // Normalize truly-empty to a valid empty JSON array so the repo's decoder is happy.
        val t = payload.trim()
        return if (t.isEmpty() || t.equals("null", ignoreCase = true))
            Result.success("[]")
        else
            Result.success(t)
    }

    // helpers to compose sample JSON ...
    private fun tx(id: String, desc: String, amount: String, code: String, indicator: String, date: String, state: String) = """
        {
          "id": "$id",
          "description": "$desc",
          "typeGroup": "Payment",
          "type": "SEPA Payment",
          "category": "Income",
          "transactionAmountCurrency": { "amount": "$amount", "currencyCode": "$code" },
          "creditDebitIndicator": "$indicator",
          "creationTime": "${date}T14:28:01",
          "state": "$state"
        }
    """.trimIndent()

    private fun singlePending(date: String) = tx("p1","Salary","1919.95","EUR","CRDT",date,"PENDING")
    private fun pendingGrouped() = listOf(
        tx("p1","Salary","1919.95","EUR","CRDT","2019-05-15","PENDING"),
        tx("p2","Bonus","200.00","EUR","CRDT","2019-05-15","PENDING"),
        tx("c2","Refund","15.00","EUR","CRDT","2019-05-19","COMPLETED"),
        tx("c22","Refund","15.00","EUR","CRDT","2019-05-17","COMPLETED"),
        tx("c222","Refund","15.00","EUR","CRDT","2019-05-18","COMPLETED"),
        tx("p33","Groceriess","55.20","EUR","DBIT","2019-05-15","PENDING"),
        tx("p333","Groceries","55.20","EUR","DBIT","2019-05-15","PENDING"),
        tx("p3333","Groceries","55.20","EUR","DBIT","2019-05-18","PENDING"),
        tx("p33333","Groceries","55.20","EUR","DBIT","2019-05-16","PENDING"),
    ).joinToString(",")
    private fun singleCompleted(date: String) = tx("c1","Payout","120.00","EUR","CRDT",date,"COMPLETED")
    private fun completedGrouped() = listOf(
        tx("c1","Refund","40.00","EUR","CRDT","2019-05-16","COMPLETED"),
        tx("c2","Refund","15.00","EUR","CRDT","2019-05-16","COMPLETED"),
        tx("c33","Taxi","22.35","EUR","DBIT","2019-05-14","COMPLETED"),
        tx("c333","Taxi","22.35","EUR","DBIT","2019-05-15","COMPLETED"),
        tx("c3333","Taxi","22.35","EUR","DBIT","2019-05-18","COMPLETED"),
        tx("c33333","Taxi","22.35","EUR","DBIT","2019-05-19","COMPLETED")
    ).joinToString(",")
    private fun mixedSingle() = listOf(
        tx("m1","Uber","12.30","EUR","DBIT","2019-05-15","PENDING"),
        tx("c2","Refund","15.00","EUR","CRDT","2019-05-16","COMPLETED"),
        tx("c3","Refund","15.00","EUR","CRDT","2019-05-16","COMPLETED"),
        tx("c4","Refund","15.00","EUR","CRDT","2019-05-15","COMPLETED"),
        tx("c5","Refund","15.00","EUR","CRDT","2019-05-16","COMPLETED"),
        tx("m2","Salary","1919.95","EUR","CRDT","2019-05-14","COMPLETED")
    ).joinToString(",")
    private fun mixedGrouped() = listOf(
        tx("g1","Uber","12.30","EUR","DBIT","2019-05-15","PENDING"),
        tx("g2","Shop","30.00","EUR","DBIT","2019-05-13","PENDING"),
        tx("g22","Shop","30.00","EUR","DBIT","2019-05-15","PENDING"),
        tx("g222","Shop","30.00","EUR","DBIT","2019-05-16","PENDING"),
        tx("g2222","Shop","30.00","EUR","DBIT","2019-05-16","PENDING"),
        tx("g3","Salary","1919.95","EUR","CRDT","2019-05-14","COMPLETED"),
        tx("g4","Refund","20.00","EUR","CRDT","2019-05-14","COMPLETED"),
        tx("g33","Salary","1919.95","EUR","CRDT","2019-05-14","COMPLETED"),
        tx("g333","Salary","1919.95","EUR","CRDT","2019-05-19","COMPLETED"),
        tx("g3333","Salary","1919.95","EUR","CRDT","2019-05-24","COMPLETED"),
    ).joinToString(",")
}