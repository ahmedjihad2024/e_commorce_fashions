package com.example.e_commorce_fashions.presentation.resources.config.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.example.e_commorce_fashions.app.utils.Di

val Scheme: ColorScheme
    @Composable
    get() = MaterialTheme.colorScheme

val TitleLarge: TextStyle
    @Composable
    get() = MaterialTheme.typography.titleLarge

val TitleMedium: TextStyle
    @Composable
    get() = MaterialTheme.typography.titleMedium

val TitleSmall: TextStyle
    @Composable
    get() = MaterialTheme.typography.titleSmall

val setTheme: (isDark: Boolean?) -> Unit = { Di.appState.setTheme(it) }
val isDark: Boolean
    @Composable
    get() = Di.appState.stateCopy.isDark ?: isSystemInDarkTheme()
