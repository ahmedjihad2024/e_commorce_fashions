package com.example.e_commorce_fashions.presentation.views.layout.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.views.cart.view.CartView
import com.example.e_commorce_fashions.presentation.views.home.view.HomeView
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeViewState
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeViewStateFactory
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.BottomAppBar
import com.example.e_commorce_fashions.presentation.views.notification.view.NotificationView
import com.example.e_commorce_fashions.presentation.views.profile.view.ProfileView
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileViewState
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileViewStateFactory

@Composable
fun LayoutView(navController: () -> NavHostController) {

    val homeViewUiState: HomeViewState = viewModel(factory = HomeViewStateFactory(Di.repository))
    val profileViewUiState: ProfileViewState = viewModel(factory = ProfileViewStateFactory(Di.repository))
    val tween = 250


    Box {
        val pagerState = rememberPagerState { 3 }
        HorizontalPager(
            modifier = Modifier
                .background(Scheme.primary)
                .fillMaxSize(),
            state = pagerState,
            key = { it }
        ) {
            when (it) {
                0 -> HomeView(navController){ homeViewUiState }
                1 -> CartView(navController)
//                2 -> NotificationView(navController)
                2 -> ProfileView(navController){ profileViewUiState }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomAppBar(currentPage = { pagerState.targetPage }, tween = tween, scrollTo = pagerState::scrollToPage)
        }
    }
}
