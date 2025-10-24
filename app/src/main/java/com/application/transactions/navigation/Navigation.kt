package com.application.transactions.navigation

private object Screens {
    const val TRANSACTIONS_SCREEN = "transaction"
}

object Destinations {
    const val TRANSACTIONS_ROUTE = "transactions/{userId}"
    fun transactions(userId: Int) = "transactions/$userId"
}
