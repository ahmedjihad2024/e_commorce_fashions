package com.example.e_commorce_fashions.presentation.views.payment_method.views.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall

@Composable
fun PairText(tOne: String, tTwo: String) {
    Text(
        buildAnnotatedString {
            append(tOne)
            pushStyle(SpanStyle(color = Scheme.onPrimary.copy(alpha = 0.5f)))
            append(tTwo)
        },
        style = TitleSmall.copy(
            color = Scheme.onPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    )
}
