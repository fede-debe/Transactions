package com.application.transactions.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.datasyncflow.LoadState
import com.application.datasyncflow.RefreshHandle
import com.application.datasyncflow.fetchAndStore
import com.application.transactions.domain.CreditDebitIndicator
import com.application.transactions.domain.Transaction
import com.application.transactions.domain.repository.TransactionsRepository
import com.application.transactions.domain.repository.usecase.BuildTransactionSectionsUseCase
import com.application.transactions.presentation.mapping.TransactionsUiMapper
import com.application.transactions.presentation.mapping.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val buildSections: BuildTransactionSectionsUseCase,
    private val uiMapper: TransactionsUiMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Int = savedStateHandle["userId"] ?: error("userId arg missing")
    private val localSource = repository.observeLocalData(userId).distinctUntilChanged()
    private val refresh = RefreshHandle()

    private val loadState: StateFlow<LoadState<List<Transaction>>> =
        fetchAndStore(
            scope = viewModelScope,
            localSource = localSource,
            fetchAndStore = { repository.fetchTransactions(userId) },
            refreshHandle = refresh
        )

    val uiState: StateFlow<TransactionsScreenState> =
        loadState
            .map { state ->
                state.toUiState(
                    buildSections = buildSections,
                    uiMapper = uiMapper
                )
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TransactionsScreenState.Loading(loadingSections)
            )

    fun fetchData() {
        viewModelScope.launch { refresh.requestRefresh() }
    }

    companion object {
        val loadingSections = listOf(
            TransactionsSectionUi.Pending(
                groups = List(8) { index ->
                    TransactionGroupUi(
                        id = "loading-$index",
                        dateText = "loadingText",
                        countLabel = "loadingText",
                        indicator = CreditDebitIndicator.CRDT,
                        fullDescriptionText = "loadingText",
                        isExpandable = false,
                        amountText = "loadingText"
                    )
                }
            )
        )
    }
}
