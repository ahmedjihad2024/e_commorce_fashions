package com.example.e_commorce_fashions.presentation.views.auth.welcome.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.common.AppLogo
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.GreatVibes
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import kotlinx.coroutines.launch

@Composable
fun AuthWelcomeView(navController: NavHostController) {

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Scheme.primary)
    ) {
        AppLogo(
            modifier = Modifier.align(Alignment.Center)
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            // Login
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Scheme.onPrimary)
                    .fillMaxWidth()
                    .height(60.dp)
                    .wrapContentSize(Alignment.Center),
                color = Color.Transparent,
                onClick = {
                   scope.launch {
                       navController.navigate(Views.LoginView.route)
                   }
                }
            ) {
                Text(
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                    text = stringResource(R.string.login),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = Scheme.primary,
                        fontSize = 16.sp,)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Sign Up
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Scheme.primary)
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(2.dp, Scheme.onPrimary, CircleShape)
                    .wrapContentSize(Alignment.Center),
                color = Color.Transparent,
                onClick = {
                    scope.launch {
                        navController.navigate(Views.SignUpView.route)
                    }
                }
            ) {
                Text(
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                    text = stringResource(R.string.sign_up),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = Scheme.onPrimary,
                        fontSize = 16.sp,)
                )
            }
        }
    }
}