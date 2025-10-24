package com.application.transactions.presentation.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.application.transactions.theme.TransactionsAppTheme
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer

@Composable
fun DirectionArrow(
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = if (isPositive) "Arrow down" else "Arrow Up",
        modifier = modifier
            .rotate(if (isPositive) 135f else -45f)
            .then(Modifier)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
    )
}


class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun DirectionArrowPreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPositive: Boolean
) {
    TransactionsAppTheme {
        DirectionArrow(isPositive = isPositive)
    }
}