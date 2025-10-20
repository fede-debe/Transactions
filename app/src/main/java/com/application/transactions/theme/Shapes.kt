package com.application.transactions.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalAppShapes = staticCompositionLocalOf { AppShapes() }

@Immutable
data class AppShapes(
    val cornerSmall: RoundedCornerShape = RoundedCornerShape(4.dp),
    val cornerMedium: RoundedCornerShape = RoundedCornerShape(8.dp),
    val cornerLarge: RoundedCornerShape = RoundedCornerShape(16.dp)
)