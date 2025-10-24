package com.application.transactions.presentation

import androidx.annotation.StringRes
import com.application.transactions.R
import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.presentation.utils.UiError

sealed interface TransactionsScreenState {

    interface HasData : TransactionsScreenState {
        val sections: List<TransactionsSectionUi>
    }

    interface HasError : TransactionsScreenState {
        val sections: List<TransactionsSectionUi>
        val uiError: UiError
    }

    data class Loading(override val sections: List<TransactionsSectionUi>) : HasData
    object Empty : TransactionsScreenState
    data class Error(override val uiError: UiError, override val sections: List<TransactionsSectionUi>) : HasError
    data class Success(override val sections: List<TransactionsSectionUi>) : HasData
}

data class TransactionGroupUi(
    val id: String,
    val dateText: String,
    val countLabel: String,
    val indicator: CreditDebitIndicator,
    val fullDescriptionText: String,
    val isExpandable: Boolean,
    val amountText: String
)

sealed interface TransactionsSectionUi {
    @get:StringRes
    val titleRes: Int
    val groups: List<TransactionGroupUi>

    data class Pending(
        override val titleRes: Int = R.string.pending,
        override val groups: List<TransactionGroupUi>
    ) : TransactionsSectionUi

    data class Completed(
        override val titleRes: Int = R.string.completed,
        override val groups: List<TransactionGroupUi>
    ) : TransactionsSectionUi
}
