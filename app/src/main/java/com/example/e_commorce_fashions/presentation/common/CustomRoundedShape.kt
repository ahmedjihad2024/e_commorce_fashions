package com.example.e_commorce_fashions.presentation.common

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalDensity

@SuppressLint("ComposableNaming")
@Composable
fun RoundedCornerShape1(
    cornerRadius: Dp,
    width: Dp,
    height: Dp
): GenericShape {

    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
    val widthPx = with(LocalDensity.current) { width.toPx() }
    val heightPx = with(LocalDensity.current) { height.toPx() }

    return GenericShape { size, _ ->
        val path = Path().apply {
            // Top-left corner
            moveTo(0f, cornerRadiusPx)
            arcTo(Rect(0f, 0f, (cornerRadiusPx * 2), (cornerRadiusPx * 2)), 180f, 90f, false)

            lineTo(size.width - cornerRadiusPx, 0f)
            arcTo(Rect(size.width - (cornerRadiusPx * 2), 0f, size.width, (cornerRadiusPx * 2)), 270f, 90f, false)

            lineTo(size.width, (size.height - heightPx) - cornerRadiusPx)
            arcTo(Rect(size.width - (cornerRadiusPx * 2), (size.height - heightPx) - (cornerRadiusPx * 2), size.width, size.height - heightPx), 0f, 90f, false)

            lineTo((size.width - widthPx), (size.height - heightPx))
            arcTo(Rect((size.width - widthPx), (size.height - heightPx), (size.width - widthPx) + (cornerRadiusPx * 2), (size.height - heightPx) + (cornerRadiusPx * 2)), 270f, -90f, false)

            lineTo(size.width - widthPx, size.height)
            arcTo(Rect((size.width - widthPx) - (cornerRadiusPx * 2), size.height - (cornerRadiusPx * 2), size.width - widthPx, size.height), 0f, 90f, false)

            lineTo(0f, size.height)
            arcTo(Rect(0f, size.height - (cornerRadiusPx * 2), (cornerRadiusPx * 2), size.height), 90f, 90f, false)
            close()
        }
        addPath(path)
    }
}


@Composable
fun roundedRectangleWithCustomCornerShape(
    topLeftRadius: Float,
    topRightRadius: Float,
    bottomRightRadius: Float,
    bottomLeftRadius: Float,
    topLeftOffsetWidth: Float = 0f,
    topLeftOffsetHeight: Float = 0f,
    topRightOffsetWidth: Float = 0f,
    topRightOffsetHeight: Float = 0f,
    bottomRightOffsetWidth: Float = 0f,
    bottomRightOffsetHeight: Float = 0f,
    bottomLeftOffsetWidth: Float = 0f,
    bottomLeftOffsetHeight: Float = 0f
): GenericShape {
    return GenericShape { size, _ ->
        val path = Path().apply {
            // Top-left corner
            moveTo(topLeftOffsetWidth, topLeftRadius + topLeftOffsetHeight)
            arcTo(
                rect = Rect(
                    topLeftOffsetWidth,
                    topLeftOffsetHeight,
                    topLeftRadius * 2 + topLeftOffsetWidth,
                    topLeftRadius * 2 + topLeftOffsetHeight
                ), startAngleDegrees = 180f, sweepAngleDegrees = 90f, forceMoveTo = false
            )

            // Top-right corner
            lineTo(size.width - topRightRadius - topRightOffsetWidth, topRightOffsetHeight)
            arcTo(
                rect = Rect(
                    size.width - topRightRadius * 2 - topRightOffsetWidth,
                    topRightOffsetHeight,
                    size.width - topRightOffsetWidth,
                    topRightRadius * 2 + topRightOffsetHeight
                ), startAngleDegrees = 270f, sweepAngleDegrees = 90f, forceMoveTo = false
            )

            // Bottom-right corner
            lineTo(
                size.width - bottomRightOffsetWidth,
                size.height - bottomRightRadius - bottomRightOffsetHeight
            )
            arcTo(
                rect = Rect(
                    size.width - bottomRightRadius * 2 - bottomRightOffsetWidth,
                    size.height - bottomRightRadius * 2 - bottomRightOffsetHeight,
                    size.width - bottomRightOffsetWidth,
                    size.height - bottomRightOffsetHeight
                ), startAngleDegrees = 0f, sweepAngleDegrees = 90f, forceMoveTo = false
            )

            // Bottom-left corner
            lineTo(bottomLeftRadius + bottomLeftOffsetWidth, size.height - bottomLeftOffsetHeight)
            arcTo(
                rect = Rect(
                    bottomLeftOffsetWidth,
                    size.height - bottomLeftRadius * 2 - bottomLeftOffsetHeight,
                    bottomLeftRadius * 2 + bottomLeftOffsetWidth,
                    size.height - bottomLeftOffsetHeight
                ), startAngleDegrees = 90f, sweepAngleDegrees = 90f, forceMoveTo = false
            )

            close()
        }
        addPath(path)
    }
}