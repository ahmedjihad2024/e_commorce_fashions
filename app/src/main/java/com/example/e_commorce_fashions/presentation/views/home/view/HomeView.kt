package com.example.e_commorce_fashions.presentation.views.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.ANIMATION_DIRECTION
import com.example.e_commorce_fashions.presentation.common.ColumnLoadable
import com.example.e_commorce_fashions.presentation.common.RowLoadable
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.common.rememberLoadableListState
import com.example.e_commorce_fashions.presentation.common.statusBarHeight
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.home.view.composable.FastButton
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeEvent
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeViewState
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardOne
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardTwo
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ViewAllProductsScreenData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.vponomarenko.compose.shimmer.shimmer

@Composable
fun HomeView(navController: () -> NavHostController, uiStateCallback: () -> HomeViewState) {

    val uiState = uiStateCallback()

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.snackBar) {
        scope.launch {
            uiState.snackBar.collectLatest {
                currentCoroutineContext().ensureActive()
                snackBarHostState.showSnackbar(it)
            }
        }
    }

    val statePage by uiState.statePage.collectAsState()
    val stateNewArrival by uiState.stateNewArrival.collectAsState()
    val statePopular by uiState.statePopular.collectAsState()


    val popularNotLoading = remember {
        derivedStateOf {
            statePopular.popularState != RequestState.LOADING
        }
    }

    val arrivalNotLoading = remember {
        derivedStateOf {
            stateNewArrival.newArrivalState != RequestState.LOADING
        }
    }

    val newArrivalRefresher = rememberLoadableListState(
        onLoadMore = { me -> uiState.onEvent(HomeEvent.LoadNewArrivalsProducts(me))
        },
    )

    LaunchedEffect(arrivalNotLoading.value) {
        newArrivalRefresher.enableLoading.value = arrivalNotLoading.value
    }

    val popularRefresher = rememberLoadableListState(
        onLoadMore = { me ->
            uiState.onEvent(HomeEvent.LoadPopularProducts(me))
        },
        onRefresh = { me ->

            // reset both loadable list when refresh
            newArrivalRefresher.reset()
            me.reset()

            uiState.onEvent(HomeEvent.ReloadPage)
            delay(300)
            me.setNotRefreshing()
        },
    )

    LaunchedEffect(popularNotLoading.value) {
        popularRefresher.enableLoading.value = popularNotLoading.value
    }

    val onClickFavorites = remember {
        { product: ProductDetails ->
            if (product.isFavorite) {
                uiState.onEvent(HomeEvent.RemoveFavorite(product))
            } else {
                uiState.onEvent(HomeEvent.AddFavorite(product))
            }
        }
    }



    Column(
        Modifier.padding(top = statusBarHeight)
    ) {

        // app bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            // menu icon
//            FastButton(
//                onClick = {
//
//                },
//                color = Scheme.onPrimary,
//                icon = R.drawable.ic_menu,
//                iconColor = Scheme.primary
//            )
            // search icon
            FastButton(
                onClick = {
                    navController().navigate(Views.SearchView.route)
                },
                color = Scheme.primary,
                icon = R.drawable.ic_search,
                iconColor = Scheme.onPrimary,
            )
        }


        SetState(
            requestState = statePage.requestState,
            onSuccess = {
                ColumnLoadable(
                    contentPadding = PaddingValues(bottom = 70.dp + 20.dp),
                    state = popularRefresher,
                ) {
                    // new arrivals
                    item("key1") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {

                            val onViewAllNewArrival: () -> Unit = remember {
                                {
                                    scope.launch {
                                        Di.sharedData.value.setData( "view-all-products",
                                            ViewAllProductsScreenData(
                                                products = stateNewArrival.newArrivalProducts,
                                                screenTitle = context.getString(R.string.new_arrivals),
                                                lastDocumentSnapshot = uiState.lastNewArrivalDocument()
                                            )
                                        )
                                        navController().navigate(Views.ViewAllView.route)
                                    }
                                }
                            }
                            Text(
                                text = stringResource(R.string.new_arrivals),
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Scheme.onPrimary,
                                    fontSize = 20.sp
                                ),
                            )
                            Text(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onViewAllNewArrival),
                                text = stringResource(R.string.view_all),
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Scheme.onPrimary.copy(.4f),
                                    fontSize = 12.sp
                                ),
                            )
                        }
                    }

                    item("key2") {
                        SetState(
                            requestState = stateNewArrival.newArrivalState,
                            animationDirection = ANIMATION_DIRECTION.LEFT_TO_RIGHT,
                            onSuccess = {

                                RowLoadable(
                                    state = newArrivalRefresher,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                                ) {
                                    items(stateNewArrival.newArrivalProducts, key = { it.productReference?.id ?: "KEY-NONE" }) { item ->
                                        ProductCardOne({ item }, navController, onClickFavorites)
                                    }
                                }
                            },
                            onLoading = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                                ) {
                                    repeat(4) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(18.dp))
                                                .shimmer()
                                                .background(
                                                    Scheme.onPrimary.copy(alpha = 0.04f)
                                                )
                                                .width(160.dp)
                                                .height(220.dp),
                                        ) {}
                                    }
                                }
                            }
                        )
                    }

                    // popular
                    item("key3") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 20.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = stringResource(R.string.popular),
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Scheme.onPrimary,
                                    fontSize = 20.sp
                                ),
                            )
                        }
                    }

                    item("key4") {
                        SetState(
                            requestState = statePopular.popularState,
                            onSuccess = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(15.dp)
                                ) {
                                    for (item in statePopular.popularProducts) {
                                        ProductCardTwo(item, navController)
                                    }
                                }
                            },
                            onLoading = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(15.dp)
                                ) {
                                    repeat(5) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(18.dp))
                                                .shimmer()
                                                .background(
                                                    Scheme.onPrimary.copy(alpha = 0.04f)
                                                )
                                                .fillMaxWidth()
                                                .height(80.dp),
                                        ) {}
                                    }
                                }
                            }
                        )
                    }

                }
            },
            onError = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        statePage.errorMessage!!,
                        style = TitleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Scheme.onPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                uiState.onEvent(HomeEvent.ReloadPage)
                            }
                            .background(Scheme.primary)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            stringResource(R.string.try_again),
                            style = TitleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Scheme.onPrimary,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            },
            onLoading = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                ) {
                    CircularProgressIndicator(
                        color = Scheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        )
    }
}

