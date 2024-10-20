package com.example.e_commorce_fashions.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize


fun Modifier.shimmerEffect(
    duration: Int = 2000,
    shimmerColors: List<Color>? = null
): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val defaultShimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.2f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer-animation")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 10 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(duration)
        ), label = "shimmer-animation"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors ?: defaultShimmerColors,
            start = Offset.Zero,
            end = Offset(startOffsetX + size.width.toFloat(), 0f)
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}