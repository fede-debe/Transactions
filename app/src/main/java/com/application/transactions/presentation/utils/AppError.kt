package com.application.transactions.presentation.utils

import androidx.annotation.StringRes
import com.application.transactions.R
import kotlinx.serialization.SerializationException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

sealed class AppError(@StringRes val messageRes: Int) {
    object NoInternet : AppError(R.string.err_no_internet)
    object Timeout : AppError(R.string.err_timeout)
    object Server : AppError(R.string.err_server)
    object Client : AppError(R.string.err_client)
    object Parsing : AppError(R.string.err_parsing)
    object Unknown : AppError(R.string.err_unknown)
}
data class UiError(@StringRes val messageRes: Int)
fun Throwable.asUiError(): UiError = when (this) {
    is UnknownHostException, is ConnectException, is SSLException ->
        UiError(AppError.NoInternet.messageRes)

    is SocketTimeoutException ->
        UiError(AppError.Timeout.messageRes)

    is EOFException, is SerializationException ->
        UiError(AppError.Parsing.messageRes)

    // TODO: optionally inspect HTTP codes here and map to Server/Client

    else ->
        UiError(AppError.Unknown.messageRes)
}