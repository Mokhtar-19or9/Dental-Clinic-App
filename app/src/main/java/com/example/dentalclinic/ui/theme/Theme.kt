package com.example.dentalclinic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightDentalColorScheme = lightColorScheme(
    primary = DentalTeal,
    onPrimary = Color.White,
    secondary = DentalCyan,
    background = DentalBackground,
    onBackground = DentalText,
    surface = DentalSurface,
    onSurface = DentalText,
    outline = DentalLine
)

private val DarkDentalColorScheme = darkColorScheme(
    primary = DentalCyan,
    onPrimary = Color.Black,
    secondary = DentalTeal,
    background = DentalBackgroundDark,
    onBackground = DentalTextDark,
    surface = DentalSurfaceDark,
    onSurface = DentalTextDark,
    outline = DentalLineDark
)

private val DentalShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun DentalClinicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkDentalColorScheme else LightDentalColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DentalTypography,
        shapes = DentalShapes,
        content = content
    )
}
