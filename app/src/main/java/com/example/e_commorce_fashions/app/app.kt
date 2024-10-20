package com.example.e_commorce_fashions.app

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.e_commorce_fashions.MainActivity
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.resources.config.navigator.AppNavigator
import com.example.e_commorce_fashions.presentation.resources.config.theme.AppTheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme

@SuppressLint("CompositionLocalNaming")
val WINDOW_SIZE = compositionLocalOf<WindowSizeClass> { error("No WindowSizeClass provided") }
val LOCAL_MAIN_ACTIVITY = compositionLocalOf<MainActivity> { error("No MainActivity provided") }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App(
    activity: MainActivity
) {
    val windowSize = calculateWindowSizeClass(activity)

    CompositionLocalProvider(
        WINDOW_SIZE provides windowSize, LOCAL_MAIN_ACTIVITY provides activity
    ) {
        AppTheme(
            dynamicColor = false,
            darkTheme = Di.appState.state.collectAsState().value.isDark
        ) {
            val transparentTextSelectionColors = TextSelectionColors(
                handleColor = Scheme.onPrimary,
                backgroundColor = Scheme.onPrimary.copy(.1f)
            )
            CompositionLocalProvider(
                LocalTextSelectionColors provides transparentTextSelectionColors
            ) {
                Box(
                    Modifier
                        .background(Scheme.primary)
                        .fillMaxSize()
                ) {
                    AppNavigator()
                }
            }
        }
    }
}


//val lambda = @DontMemoize {
//    ...
//}