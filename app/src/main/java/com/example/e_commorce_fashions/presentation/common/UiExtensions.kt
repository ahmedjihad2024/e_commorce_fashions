package com.example.e_commorce_fashions.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


val statusBarHeight: Dp @Composable
get() {
    val view = LocalView.current
    val insets = ViewCompat.getRootWindowInsets(view)
    val statusBarHeight = with(LocalDensity.current) {
        insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top?.toDp() ?: 0.dp
    }
    return statusBarHeight
}