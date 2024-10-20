package com.example.e_commorce_fashions.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.presentation.resources.config.theme.GreatVibes
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall

@Composable
fun AppLogo(
    modifier: Modifier = Modifier
){
    Box(
        modifier
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                stringResource(R.string.app_name), style = TitleSmall.copy(
                    fontFamily = FontFamily.GreatVibes,
                    fontSize = 50.sp,
                    color = Scheme.onPrimary
                )
            )
            Text(
                modifier = Modifier.offset(y = (-18).dp),
                text = stringResource(R.string.my_life_my_style), style = TitleSmall.copy(
                    fontSize = 12.3.sp,
                    lineHeight = 5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Scheme.onPrimary
                )
            )
        }
    }
}