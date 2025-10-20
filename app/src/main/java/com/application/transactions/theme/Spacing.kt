package com.application.transactions.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 32.dp,

    // Page spacing helpers
    val betweenCards: Dp = 16.dp,
    val betweenSections: Dp = 24.dp,
    val betweenHeaderAndSection: Dp = 16.dp,

    // Card spacing
    val borderRadius: Dp = large,
    val cardPadding: Dp = medium,
    val cardContentSpacing: Dp = small
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }