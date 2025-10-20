package com.application.datasyncflow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException

/**
 * Orchestrates local source-of-truth + fetch side effect into StateFlow<LoadState<T>>.
 *
 * Guarantees:
 * - On first collect (by default), triggers exactly one refresh.
 * - While a fetch is in flight, extra refresh requests are *dropped* (true conflation).
 * - After successful fetch, emits Ready with either the latest local OR the fetched value
 *   (so you never get stuck in Loading(null) if the DB write is delayed or empty).
 * - On error, emits Error(latestLocal, cause).
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> fetchAndStore(
    scope: CoroutineScope,
    localSource: Flow<T?>,
    fetchAndStore: suspend () -> Result<T>,
    refreshHandle: RefreshHandle = RefreshHandle(),
    startWithInitialRefresh: Boolean = true,
    sharing: SharingStarted = SharingStarted.WhileSubscribed(5_000),
    initial: LoadState<T> = LoadState.Loading(null)
): StateFlow<LoadState<T>> {

    // 1) Refresh stream (optionally auto-start) — conflate bursts early.
    val refreshes: Flow<Unit> =
        refreshHandle.requests
            .let { if (startWithInitialRefresh) merge(flowOf(Unit), it) else it }
            .buffer(Channel.CONFLATED)

    // 2) Fetch status with a simple "busy" gate that DROPS extra refreshes while running.
    val busy = AtomicBoolean(false)

    val status: Flow<FetchStatus<T>> =
        refreshes
            .filter { busy.compareAndSet(false, true) }   // drop while busy
            .flatMapLatest {
                flow {
                    emit(FetchStatus.Fetching)
                    val out = try {
                        val result = fetchAndStore()
                        result.fold(
                            onSuccess = { value -> FetchStatus.Success(value) },
                            onFailure = { err -> FetchStatus.Error(err) }
                        )
                    } catch (c: CancellationException) {
                        busy.set(false)
                        throw c
                    } catch (t: Throwable) {
                        FetchStatus.Error(t)
                    } finally {
                        // mark gate open for next refresh
                        busy.set(false)
                    }
                    emit(out)
                }
            }
            .onStart { emit(FetchStatus.Idle) }

    // 3) Combine local + status into a minimal, obvious LoadState.
    val combined: Flow<LoadState<T>> =
        combine(
            localSource.onStart { emit(null) }.distinctUntilChanged(),
            status
        ) { local, s ->
            when (s) {
                FetchStatus.Idle,
                FetchStatus.Fetching -> LoadState.Loading(local)
                is FetchStatus.Success -> {
                    // Prefer local if present; otherwise use fetched payload immediately.
                    val ready = local ?: s.data
                    LoadState.Ready(ready)
                }
                is FetchStatus.Error -> LoadState.Error(local, s.cause)
            }
        }.distinctUntilChanged()

    return combined.stateIn(scope, sharing, initial)
}

sealed interface FetchStatus<out T> {
    data object Idle : FetchStatus<Nothing>
    data object Fetching : FetchStatus<Nothing>
    data class Success<T>(val data: T) : FetchStatus<T>
    data class Error(val cause: Throwable) : FetchStatus<Nothing>
}
