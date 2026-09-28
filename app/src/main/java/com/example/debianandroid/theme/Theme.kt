package com.example.debianandroid.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WinlatorColorScheme = darkColorScheme(
    primary = WinlatorBlue,
    onPrimary = TextPrimary,
    primaryContainer = WinlatorBlueContainer,
    onPrimaryContainer = WinlatorBlueLight,
    secondary = AccentCyan,
    onSecondary = WinlatorDarkBg,
    secondaryContainer = WinlatorCard,
    onSecondaryContainer = AccentCyan,
    tertiary = AccentGreen,
    onTertiary = WinlatorDarkBg,
    background = WinlatorDarkBg,
    onBackground = TextPrimary,
    surface = WinlatorSurface,
    onSurface = TextPrimary,
    surfaceVariant = WinlatorCard,
    onSurfaceVariant = TextSecondary,
    outline = WinlatorBorder,
    outlineVariant = WinlatorCardElevated
)

@Composable
fun DebianAndroidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WinlatorColorScheme,
        typography = Typography,
        content = content
    )
}
