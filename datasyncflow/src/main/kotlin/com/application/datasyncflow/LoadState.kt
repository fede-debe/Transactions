package com.application.datasyncflow

/** Result of loading observable data. Keep this sealed type tiny and obvious. */
sealed interface LoadState<out T> {
    /** Data is present and ready to display. */
    data class Ready<T>(val data: T) : LoadState<T>

    /** A load is in progress. If data is non-null, show it while loading. */
    data class Loading<T>(val data: T?) : LoadState<T>

    /** A load failed. If data is non-null, show fallback UI + error. */
    data class Error<T>(val data: T?, val cause: Throwable) : LoadState<T>
}