package com.korbuts.spigun.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class SpigunColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val primaryContainer: Color,
    val error: Color,
    val gray: Color,
    val success: Color
)

private val DarkSpigunColors = SpigunColors(
    primary = SpigunWhite,
    onPrimary = SpigunBlack,
    background = SpigunBlack,
    onBackground = SpigunWhite,
    surface = SpigunBlack,
    onSurface = SpigunWhite,
    surfaceVariant = DarkGrey800,
    onSurfaceVariant = SpigunWhite,
    secondaryContainer = DarkGrey700,
    onSecondaryContainer = SpigunWhite,
    primaryContainer = DarkGrey600,
    error = ErrorRed,
    gray = SpigunGray,
    success = SuccessGreen
)

private val LightSpigunColors = DarkSpigunColors.copy()

val LocalSpigunColors = staticCompositionLocalOf<SpigunColors> {
    error("No SpigunColors provided")
}

object SpigunTheme {
    val colors: SpigunColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSpigunColors.current
    
    val typography = Typography
}

private val DarkColorScheme = darkColorScheme(
    primary = SpigunWhite,
    onPrimary = SpigunBlack,
    secondary = SpigunWhite,
    onSecondary = SpigunBlack,
    tertiary = SpigunGray,
    background = SpigunBlack,
    onBackground = SpigunWhite,
    surface = SpigunBlack,
    onSurface = SpigunWhite,
    surfaceVariant = DarkGrey800,
    onSurfaceVariant = SpigunWhite,
    secondaryContainer = DarkGrey700,
    onSecondaryContainer = SpigunWhite,
    primaryContainer = DarkGrey600,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = SpigunWhite,
    onPrimary = SpigunBlack,
    secondary = SpigunWhite,
    onSecondary = SpigunBlack,
    tertiary = SpigunGray,
    background = SpigunBlack,
    onBackground = SpigunWhite,
    surface = SpigunBlack,
    onSurface = SpigunWhite,
    surfaceVariant = DarkGrey800,
    onSurfaceVariant = SpigunWhite,
    secondaryContainer = DarkGrey700,
    onSecondaryContainer = SpigunWhite,
    primaryContainer = DarkGrey600,
    error = ErrorRed
)

@Composable
fun SpigunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val spigunColors = if (darkTheme) DarkSpigunColors else LightSpigunColors

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalSpigunColors provides spigunColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
