package com.application.transactions.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColors = lightColorScheme(
    primary = primary_light,
    onPrimary = onPrimary_light,
    background = surfacePrimary_light,
    surface = surfacePrimary_light,
    onSurface = textDefault_light,
    surfaceVariant = foundation_light,
    onSurfaceVariant = textSupport_light,
    outline = separator_light,
    tertiary = success_light
)

val DarkColors = darkColorScheme(
    primary = primary_dark,
    onPrimary = onPrimary_dark,
    background = surfacePrimary_dark,
    surface = surfacePrimary_dark,
    onSurface = textDefault_dark,
    surfaceVariant = foundation_dark,
    onSurfaceVariant = textSupport_dark,
    outline = separator_dark,
    tertiary = success_dark
)