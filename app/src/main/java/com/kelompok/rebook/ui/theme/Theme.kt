package com.kelompok.rebook.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = Primary40,
    onPrimary            = White,
    primaryContainer     = PrimaryContainer40,
    onPrimaryContainer   = Primary20,
    secondary            = Secondary40,
    onSecondary          = White,
    tertiary             = Tertiary40,
    background           = PaperLight,
    surface              = PaperLight,
    onBackground         = InkDark,
    onSurface            = InkDark,
    surfaceVariant       = NeutralVariant90,
    onSurfaceVariant     = NeutralVariant30,
)

@Composable
fun ReBookTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ReBookTypography,
        content     = content
    )
}
