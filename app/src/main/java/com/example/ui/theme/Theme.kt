package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GeometricBlue,
    secondary = TealAccent,
    tertiary = TrendGreen,
    background = BaseLightBg,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextDarkSlate,
    onSurface = TextDarkSlate,
    error = AlertCoral
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Set default to false (Light Canvas with geometric card balance)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
