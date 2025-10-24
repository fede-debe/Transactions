package com.application.transactions.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.application.transactions.theme.AppThemeTokens
import com.application.transactions.theme.TransactionsAppTheme

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    image: ImageVector,
    title: String,
    subtitle: String,
    button: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = AppThemeTokens.spacing.large),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = AppThemeTokens.shapes.cornerLarge
    ) {
        Column(
            modifier = modifier
                .wrapContentSize()
                .padding(AppThemeTokens.spacing.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = AppThemeTokens.spacing.medium),
                imageVector = image,
                contentDescription = "Info card image",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppThemeTokens.spacing.small),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppThemeTokens.spacing.large),
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (button != null) {
                button()
            }
        }
    }
}

@PreviewLightDark
@Preview(showBackground = true, name = "InfoCard Preview")
@Composable
fun InfoCardPreview(
    @PreviewParameter(InfoCardVariant::class) image: ImageVector
) {
    TransactionsAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppThemeTokens.spacing.small),
            contentAlignment = Alignment.Center
        )
        {
            InfoCard(
                image = image,
                title = "No transactions available",
                subtitle = "InfoSubtitle"
            )
        }
    }
}

class InfoCardVariant : PreviewParameterProvider<ImageVector> {
    override val values: Sequence<ImageVector> =
        sequenceOf(Icons.Filled.AccountCircle, Icons.TwoTone.Warning)
}