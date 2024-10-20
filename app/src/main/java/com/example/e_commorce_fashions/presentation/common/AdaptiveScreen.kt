package com.example.e_commorce_fashions.presentation.common

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commorce_fashions.app.WINDOW_SIZE

@Composable
fun AdaptiveScreen(
    compact: @Composable() () -> Unit,
    medium: (@Composable() () -> Unit)?,
    expanded: (@Composable() () -> Unit)?

) {
    when (WINDOW_SIZE.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact()
        WindowWidthSizeClass.Medium -> medium?.invoke() ?: compact()
        WindowWidthSizeClass.Expanded -> expanded?.invoke() ?: medium?.invoke() ?: compact()
    }
}

object SizeDp {

    val height: Int
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            return configuration.screenHeightDp
        }

    val width: Int
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            return configuration.screenWidthDp
        }
}

object SizePx {
    val height: Int
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            return with(density) { configuration.screenHeightDp.dp.roundToPx() }
        }

    val width: Int
        @Composable
        get() {
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            return with(density) { configuration.screenWidthDp.dp.roundToPx() }
        }
}


// This can be particularly useful for graphics, icons, and text
val Number.sd: TextUnit
    @Composable
    get() {
        val density = LocalDensity.current
        return (this.toFloat() * density.density).sp
    }

val Number.ar: Dp
    @Composable
    get() {
        val aspectRatio = SizeDp.height.toFloat() / SizeDp.height.toFloat()
        return (this.toFloat() * aspectRatio).dp
    }

val Number.sh: Dp
    @Composable
    get() {
        return (SizeDp.height * this.toFloat()).dp
    }

val Number.sw: Dp
    @Composable
    get() {
        return (SizeDp.width * this.toFloat()).dp
    }


val Dp.toPxFloat: Float
    @Composable get() = with(LocalDensity.current) { this@toPxFloat.toPx() }

val Float.toDp: Dp
    @Composable get() = with(LocalDensity.current) { this@toDp.toDp() }

