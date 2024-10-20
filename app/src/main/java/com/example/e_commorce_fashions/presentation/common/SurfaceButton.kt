package com.example.e_commorce_fashions.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme

@Composable
fun SurfaceButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.wrapContentSize(Alignment.Center)
            .size(55.dp)
            ,
        shape = CircleShape,
        color = Scheme.onSecondary.copy(.07f),
        onClick = {
            onClick()
        }
    ) {
        icon()
    }
}