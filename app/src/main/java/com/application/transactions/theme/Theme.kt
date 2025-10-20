package com.application.transactions.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.application.transactions.theme.colors.DarkColors
import com.application.transactions.theme.colors.LightColors

val AppTypography = Typography(
    // Section heading
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    ),
    // Navigation heading (17)
    titleMedium = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp
    ),
    // Card heading
    labelLarge = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    ),
    // Card content
    bodySmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
    ),
    // Card amount
    headlineSmall = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
)

@Composable
fun TransactionsAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacing(),
        LocalAppShapes provides AppShapes()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            content = content
        )
    }
}

object AppThemeTokens {
    val spacing @Composable get() = LocalAppSpacing.current
    val shapes @Composable get() = LocalAppShapes.current
}