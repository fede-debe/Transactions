package com.application.transactions.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.application.transactions.R
import com.application.transactions.presentation.utils.UiError

@Composable
fun InfoStateContent(state: InfoState, paddingValues: PaddingValues) {

    val cardData = when (state) {
        is InfoState.GenericError -> InfoCardData(
            image = Icons.TwoTone.Warning,
            title = stringResource(R.string.something_went_wrong),
            subtitle = stringResource(state.uiError.messageRes)
        )
        is InfoState.EmptyContent -> InfoCardData(
            image = Icons.Outlined.Info,
            title = stringResource(R.string.no_transactions),
            subtitle = stringResource(R.string.transactions_empty_state_title)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                cardData.apply {
                    InfoCard(
                        image = image,
                        title = title,
                        subtitle = subtitle
                    )
                }
            }
        }
    }
}

/**
 * Represents the distinct states for the informational screen.
 */
sealed class InfoState {
    object EmptyContent : InfoState()
    data class GenericError(val uiError: UiError) : InfoState()

    // data class NoInternet(...) : InfoState()
}

private data class InfoCardData(
    val image: ImageVector,
    val title: String,
    val subtitle: String
)