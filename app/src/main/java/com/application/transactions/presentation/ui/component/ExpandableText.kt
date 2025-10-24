package com.application.transactions.presentation.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import com.application.transactions.theme.AppThemeTokens
import com.application.transactions.theme.TransactionsAppTheme
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer

@Composable
fun ExpandableText(
    text: String,
    isExpandable: Boolean,
    expanded: Boolean,
    isLoading: Boolean = false,
    onExpandedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(AppThemeTokens.shapes.cornerSmall)
            .let { if (isExpandable) it.clickable { onExpandedChange(!expanded) } else it }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            modifier = Modifier.padding(horizontal = AppThemeTokens.spacing.small)
        )

        if (isExpandable && !expanded) {
            Text(
                text = "…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = AppThemeTokens.spacing.small)
            )
        }
    }
}

data class ExpandableTextPreviewData(
    val descriptions: List<String>,
    val expanded: Boolean
) {
    val fullDescriptionText: String = descriptions.joinToString("\n")
    val isExpandable: Boolean = descriptions.size > 3
}

class ExpandableTextPreviewProvider : PreviewParameterProvider<ExpandableTextPreviewData> {
    override val values = sequenceOf(
        ExpandableTextPreviewData(
            descriptions = listOf("Uber", "Mediamarkt"),
            expanded = false
        ),
        ExpandableTextPreviewData(
            descriptions = listOf("Uber", "Mediamarkt", "Spotify", "Amazon", "Bol.com"),
            expanded = false
        ),
        ExpandableTextPreviewData(
            descriptions = listOf("Uber", "Mediamarkt", "Spotify", "Amazon", "Bol.com"),
            expanded = true
        )
    )
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun ExpandableTextPreview(
    @PreviewParameter(ExpandableTextPreviewProvider::class) data: ExpandableTextPreviewData
) {
    TransactionsAppTheme {
        ExpandableText(
            text = data.fullDescriptionText,
            isExpandable = data.isExpandable,
            expanded = data.expanded,
            onExpandedChange = {}
        )
    }
}

