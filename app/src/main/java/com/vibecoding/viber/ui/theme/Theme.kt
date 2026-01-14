package com.vibecoding.viber.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GitHubBlue,
    secondary = VibePurple,
    tertiary = VibePink,
    background = GitHubDark,
    surface = GitHubGray,
    onPrimary = GitHubDark,
    onSecondary = GitHubDark,
    onTertiary = GitHubDark,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val VibeModeColorScheme = darkColorScheme(
    primary = VibePurple,
    secondary = VibeBlue,
    tertiary = VibePink,
    background = Color(0xFF1A0033),
    surface = Color(0xFF2D0052),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val CatModeColorScheme = darkColorScheme(
    primary = CatOrange,
    secondary = CatBeige,
    tertiary = VibePink,
    background = Color(0xFF2C1810),
    surface = Color(0xFF3D2415),
    onPrimary = Color.White,
    onSecondary = GitHubDark,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun ViberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    vibeMode: Boolean = false,
    catMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        catMode -> CatModeColorScheme
        vibeMode -> VibeModeColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
