package com.application.datasyncflow

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Emits refresh requests (e.g., pull-to-refresh).
 * Internal buffering keeps the common case simple; callers don’t manage channels/flows.
 */
class RefreshHandle(
    replay: Int = 0,
    extraBufferCapacity: Int = 1
) {
    private val _requests = MutableSharedFlow<Unit>(replay, extraBufferCapacity)
    internal val requests: SharedFlow<Unit> = _requests.asSharedFlow()

    /** Ask the system to refresh remote data. Safe to call from UI. */
    suspend fun requestRefresh() { _requests.emit(Unit) }
}