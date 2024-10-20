package com.example.e_commorce_fashions.presentation.views.home.view.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun FastButton(
    onClick: () -> Unit,
    color: Color,
    iconColor: Color,
    @DrawableRes icon: Int,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(color)
            .size(43.dp)
            .padding(10.dp)
            .wrapContentSize(Alignment.Center),
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                icon,
                contentScale = ContentScale.Crop
            ),
            contentDescription = "menu", colorFilter = ColorFilter.tint(iconColor)
        )
    }
}
