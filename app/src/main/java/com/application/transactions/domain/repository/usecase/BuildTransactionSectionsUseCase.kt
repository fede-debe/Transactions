package com.application.transactions.domain.repository.usecase

import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.domain.Transaction
import com.application.transactions.domain.TransactionGroup
import com.application.transactions.domain.TransactionState
import com.application.transactions.domain.TransactionsSection
import java.time.LocalDate

class BuildTransactionSectionsUseCase(
    private val sectionOrder: List<TransactionState> =
        listOf(TransactionState.PENDING, TransactionState.COMPLETED),
    private val groupComparator: Comparator<TransactionGroup> =
        compareByDescending<TransactionGroup> { it.date }
            .thenBy { it.creditDebitIndicator.name }
) {

    private data class GroupKey(
        val state: TransactionState,
        val date: LocalDate,
        val indicator: CreditDebitIndicator
    )

    operator fun invoke(items: List<Transaction>): List<TransactionsSection> {
        if (items.isEmpty()) return emptyList()

        // 1) Group by (state, date, indicator)
        val groupedByKey: Map<GroupKey, List<Transaction>> =
            items.groupBy { GroupKey(it.state, it.creationDate, it.creditDebitIndicator) }

        // 2) Turn each bucket into a TransactionGroup using only key values
        //    and then bucket those groups by state
        val byState: Map<TransactionState, List<TransactionGroup>> =
            groupedByKey
                .map { (groupKey, list) -> groupKey.state to TransactionGroup(groupKey.date, groupKey.indicator, list) }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                .mapValues { (_, groups) -> groups.sortedWith(groupComparator) }

        // 3) Build sections in desired order, skipping empties
        return sectionOrder.mapNotNull { state ->
            byState[state]?.let { groups ->
                when (state) {
                    TransactionState.PENDING   -> TransactionsSection.Pending(groups)
                    TransactionState.COMPLETED -> TransactionsSection.Completed(groups)
                }
            }
        }
    }
}

