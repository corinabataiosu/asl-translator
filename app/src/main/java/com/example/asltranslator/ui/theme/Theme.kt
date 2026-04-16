package com.example.asltranslator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val bg_light = Color(0xFFF8FAFC)
val surface_light = Color(0xFFFFFFFF)
val text_light = Color(0xFF0F172A)

val bg_navy = Color(0xFF0A1128)
val surface_navy = Color(0xFF1B2740)
val text_navy = Color(0xFFF1F5F9)
val stroke_navy = Color(0xFF3A4B6B)
val stroke_light = Color(0xFFCBD5E1)
val accent = Color(0xFF3B82F6)

private val DarkColorScheme = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    background = bg_navy,
    surface = surface_navy,
    onBackground = text_navy,
    onSurface = text_navy,
    outline = stroke_navy
)

private val LightColorScheme = lightColorScheme(
    primary = accent,
    onPrimary = Color.White,
    background = bg_light,
    surface = surface_light,
    onBackground = text_light,
    onSurface = text_light,
    outline = stroke_light
)

@Composable
fun ASLTranslatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
