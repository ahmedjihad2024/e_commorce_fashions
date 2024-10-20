package com.example.e_commorce_fashions.presentation.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.e_commorce_fashions.app.utils.RequestState

enum class ANIMATION_DIRECTION{
    BOTTOM_TO_TOP,
    LEFT_TO_RIGHT,
}

@Composable
fun SetState(
    requestState: RequestState,
    animationDirection: ANIMATION_DIRECTION = ANIMATION_DIRECTION.BOTTOM_TO_TOP,
    onSuccess: @Composable () -> Unit,
    onError: (@Composable () -> Unit)? = null,
    onEmpty: (@Composable () -> Unit)? = null,
    onNoInternet: (@Composable () -> Unit)? = null,
    onLoading: (@Composable () -> Unit)? = null,
    onIdle: (@Composable () -> Unit)? = null
) {

    val height = with(LocalDensity.current) { 40.dp.toPx() }

    AnimatedContent(targetState = requestState, label = "set-state",
        transitionSpec = {
        if(animationDirection == ANIMATION_DIRECTION.BOTTOM_TO_TOP){
            if (targetState == RequestState.SUCCESS) {
                (slideInVertically { height.toInt() } + fadeIn()) togetherWith
                        slideOutVertically {  -height.toInt() } + fadeOut()
            } else {
                (slideInVertically { -height.toInt() } + fadeIn()) togetherWith
                        slideOutVertically { height.toInt() } + fadeOut()
            }
        }else{
            if (targetState == RequestState.SUCCESS) {
                (slideInHorizontally { height.toInt() } + fadeIn()) togetherWith
                        slideOutHorizontally {  -height.toInt() } + fadeOut()
            } else {
                (slideInHorizontally  { -height.toInt() } + fadeIn()) togetherWith
                        slideOutHorizontally  { height.toInt() } + fadeOut()
            }
        }
    }) { state ->
        when (state) {
            is RequestState.SUCCESS -> onSuccess()
            is RequestState.ERROR -> onError?.invoke()
            is RequestState.EMPTY -> onEmpty?.invoke()
            is RequestState.NO_INTERNET -> onNoInternet?.invoke()
            is RequestState.LOADING -> onLoading?.invoke()
            is RequestState.IDLE -> onIdle?.invoke()
        }
    }
}