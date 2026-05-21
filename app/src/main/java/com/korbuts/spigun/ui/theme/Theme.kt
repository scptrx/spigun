package com.korbuts.spigun.ui.theme

import android.app.Activity
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
    val gray: Color = Color.Gray,
    val success: Color
)

val LocalSpigunColors = staticCompositionLocalOf {
    SpigunColors(
        primary = Color.White,
        onPrimary = Color.Black,
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF1E1E1E),
        onSurfaceVariant = Color.White,
        secondaryContainer = Color(0xFF333333),
        onSecondaryContainer = Color.White,
        primaryContainer = Color(0xFF444444),
        error = Color(0xFF703D49),
        success = Color(0xFF62B04F)
    )
}

object SpigunTheme {
    val colors: SpigunColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSpigunColors.current
    
    val typography = Typography
}

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    tertiary = Color.Gray,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color.White,
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color.White,
    primaryContainer = Color(0xFF444444),
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    tertiary = Color.Gray,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color.White,
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color.White,
    primaryContainer = Color(0xFF444444),
    error = Color(0xFFCF6679)
)

@Composable
fun SpigunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        SpigunColors(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E1E1E),
            onSurfaceVariant = Color.White,
            secondaryContainer = Color(0xFF333333),
            onSecondaryContainer = Color.White,
            primaryContainer = Color(0xFF444444),
            error = Color(0xFFCF6679),
            success = Color(0xFF62B04F)
        )
    } else {
        SpigunColors(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E1E1E),
            onSurfaceVariant = Color.White,
            secondaryContainer = Color(0xFF333333),
            onSecondaryContainer = Color.White,
            primaryContainer = Color(0xFF444444),
            error = Color(0xFFCF6679),
            success = Color(0xFF62B04F)
        )
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalSpigunColors provides colors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
