package com.example.e_commorce_fashions.presentation.resources.config.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )
)
