package com.example.debianandroid.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DebianRed,
    onPrimary = TextPrimary,
    primaryContainer = DebianRedContainer,
    onPrimaryContainer = DebianRedLight,
    secondary = AccentCyan,
    onSecondary = DarkSurface,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = AccentCyan,
    tertiary = AccentGreen,
    onTertiary = DarkSurface,
    background = DarkSurface,
    onBackground = TextPrimary,
    surface = DarkSurfaceVariant,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = DarkSurfaceVariant
)

@Composable
fun DebianAndroidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
