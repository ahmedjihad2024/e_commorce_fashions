package com.example.e_commorce_fashions.presentation.views.splash.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.LOCAL_MAIN_ACTIVITY
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.common.AppLogo
import com.example.e_commorce_fashions.presentation.common.SvgImage
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.GreatVibes
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun SplashView(navController: NavHostController) {

    var isStarted by rememberSaveable { mutableStateOf(false) }
//    val scope = rememberCoroutineScope()
//    val timer = rememberUpdatedState {
//        scope.launch {
//
//        }
//    }

    LaunchedEffect(Unit) {
        if (!isStarted) {
            isStarted = true
            delay(1000 * 3)
            Di.preferences.value.isFirstLaunch().first { isFirsLaunch ->
                if (isFirsLaunch) {
                    // go to on boarding view
                    navController.navigate(Views.OnBoardingView.route) {
                        popUpTo(Views.SplashView.route) {
                            inclusive = true
                        }
                    }
                } else {
                    // check if auth or not so decide which view to go
                    // login or home
                    Di.preferences.value.isUserAuth().first { isAuth ->
                        if (isAuth) {
                            navController.navigate(Views.LayoutView.route) {
                                popUpTo(Views.SplashView.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(Views.AuthWelcomeView.route) {
                                popUpTo(Views.SplashView.route) {
                                    inclusive = true
                                }
                            }
                        }
                        isAuth
                    }
                }
                isFirsLaunch
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Scheme.primary)
            .wrapContentSize(Alignment.Center)
    ) {
        AppLogo()
    }
}

