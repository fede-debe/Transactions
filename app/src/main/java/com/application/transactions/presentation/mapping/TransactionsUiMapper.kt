package com.application.transactions.presentation.mapping

import com.application.datasyncflow.LoadState
import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.domain.Transaction
import com.application.transactions.domain.TransactionGroup
import com.application.transactions.domain.TransactionsSection
import com.application.transactions.domain.repository.usecase.BuildTransactionSectionsUseCase
import com.application.transactions.presentation.TransactionGroupUi
import com.application.transactions.presentation.TransactionsScreenState
import com.application.transactions.presentation.TransactionsSectionUi
import com.application.transactions.presentation.TransactionsViewModel.Companion.loadingSections
import com.application.transactions.presentation.utils.asUiError
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale
import kotlin.collections.orEmpty

class TransactionsUiMapper(
    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
) {
    private val currencyFormatters = mutableMapOf<String, NumberFormat>()

    fun map(sections: List<TransactionsSection>): List<TransactionsSectionUi> =
        sections.map { section ->
            val uiGroups = section.groups.map { g -> g.toUi() }
            when (section) {
                is TransactionsSection.Pending -> TransactionsSectionUi.Pending(groups = uiGroups)
                is TransactionsSection.Completed -> TransactionsSectionUi.Completed(groups = uiGroups)
            }
        }

    private fun TransactionGroup.toUi(): TransactionGroupUi {
        val currencyCode = transactions.firstOrNull()?.transaction?.currencyCode ?: DEFAULT_CURRENCY_CODE

        val fmt = currencyFormatters.getOrPut(currencyCode) {
            NumberFormat.getCurrencyInstance().apply {
                this.currency = Currency.getInstance(currencyCode)
            }
        }

        val amount = totalAmount
        val abs = amount.abs()
        val signedText = when {
            amount.signum() > 0 -> "+ ${fmt.format(abs)}"
            amount.signum() < 0 -> "- ${fmt.format(abs)}"
            else -> fmt.format(abs)
        }

        val isCredit = creditDebitIndicator == CreditDebitIndicator.CRDT

        val countLabel = buildString {
            append(count).append(' ')
            if (isCredit) {
                append(if (count == 1) "Credit" else "Credits")
            } else {
                append(if (count == 1) "Debit" else "Debits")
            }
        }

        return TransactionGroupUi(
            id = "${date}#${creditDebitIndicator.name}",
            dateText = date.format(dateFormatter),
            countLabel = countLabel,
            indicator = creditDebitIndicator,
            fullDescriptionText = descriptions.joinToString("\n"),
            isExpandable = descriptions.size > 3,
            amountText = signedText
        )
    }

    companion object {
        private const val DEFAULT_CURRENCY_CODE = "EUR"
    }
}

fun LoadState<List<Transaction>>.toUiState(
    buildSections: BuildTransactionSectionsUseCase,
    uiMapper: TransactionsUiMapper
): TransactionsScreenState {
    return when (this) {
        is LoadState.Loading -> {
            TransactionsScreenState.Loading(loadingSections)
        }

        is LoadState.Error -> {
            val cached = this.data.orEmpty()
            if (cached.isEmpty()) {
                TransactionsScreenState.Error(
                    this.cause.asUiError(),
                    sections = emptyList()
                )
            } else {
                val domain = buildSections(cached)
                if (domain.isEmpty()) TransactionsScreenState.Empty
                else TransactionsScreenState.Success(uiMapper.map(domain))
            }
        }

        is LoadState.Ready -> {
            val domain = buildSections(this.data)
            if (domain.isEmpty()) TransactionsScreenState.Empty
            else TransactionsScreenState.Success(uiMapper.map(domain))
        }
    }
}