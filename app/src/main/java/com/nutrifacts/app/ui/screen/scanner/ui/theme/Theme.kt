package com.nutrifacts.app.ui.screen.scanner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.nutrifacts.app.ui.theme.Black
import com.nutrifacts.app.ui.theme.Coral
import com.nutrifacts.app.ui.theme.DarkWhite
import com.nutrifacts.app.ui.theme.RedApple
import com.nutrifacts.app.ui.theme.White
import com.nutrifacts.app.ui.theme.YellowApple

private val DarkColorScheme = darkColorScheme(
    primary = RedApple,
    secondary = Coral,
    tertiary = YellowApple,
    background = Black,
    surface = Black,
    onPrimary = DarkWhite,
    onSecondary = DarkWhite,
    onTertiary = DarkWhite,
    onBackground = DarkWhite,
    onSurface = DarkWhite,
    surfaceVariant = Black,
    surfaceTint = Black
)

private val LightColorScheme = lightColorScheme(
    primary = RedApple,
    secondary = White,
    tertiary = YellowApple,
    background = White,
    surface = White,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black,
    surfaceVariant = White,
    surfaceTint = White
)

@Composable
fun NutrifactsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = com.nutrifacts.app.ui.theme.Typography,
        content = content
    )
}