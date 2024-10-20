package com.example.e_commorce_fashions.presentation.views.auth.success_register.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall

@Composable
fun SuccessRegisterView(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Scheme.primary)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Success",
                tint = Color.Green,
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.successfully),
                style = TitleLarge.copy(
                    color = Scheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp,
                )
            )
            Spacer(Modifier.height(5.dp))
            Text(
                modifier = Modifier.width(300.dp),
                text = stringResource(R.string.successfully_registered),
                style = TitleLarge.copy(
                    textAlign = TextAlign.Center,
                    color = Scheme.onPrimary.copy(.3f),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                )
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .background(Scheme.onPrimary)
                .fillMaxWidth()
                .height(60.dp),
            color = Color.Transparent,
            onClick = {
                navController.navigate(Views.LayoutView.route){
                    popUpTo(Views.AuthWelcomeView.route){
                        inclusive = true
                    }
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = stringResource(R.string.start_shopping),
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Scheme.primary,
                    fontSize = 16.sp,
                )
            )
        }
    }
}
