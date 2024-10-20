package com.example.e_commorce_fashions.presentation.views.view_all.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.Lazy
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LazyVerticalGridLoadable
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.common.rememberLoadableListState
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.home.view.composable.FastButton
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeEvent
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardOne
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardTwo
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ViewAllProductsScreenData
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsEvent
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsState
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsStateView
import kotlinx.coroutines.delay
import me.vponomarenko.compose.shimmer.shimmer
import retrofit2.http.Body


@Composable
fun ViewAllProductsView(navController: () -> NavHostController, uiState:ViewAllProductsStateView){
    val sharedData: ViewAllProductsScreenData? = remember {
        Di.sharedData.value.getData<ViewAllProductsScreenData>("view-all-products")
    }
    val state = uiState.state.collectAsStateWithLifecycle()

    SideEffect {
        uiState.onEvent(ViewAllProductsEvent.Init)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Scheme.primary,
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            horizontalAlignment = Alignment.Start
        ){
            // app bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            navController().navigateUp()
                        }
                        .background(Scheme.onPrimary)
                        .size(50.dp)
                        .padding(15.dp)
                        .wrapContentSize(Alignment.Center),
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            R.drawable.ic_arrow_back,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = "menu", colorFilter = ColorFilter.tint(Scheme.primary)
                    )
                }

                Text(
                    text = sharedData!!.screenTitle,
                    modifier = Modifier.padding(top = 10.dp),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Scheme.onPrimary,
                        fontSize = 18.sp
                    )
                )
            }

            Body(
                navController = navController,
                theState = { state },
                onEvent = uiState::onEvent
            )

        }
    }
}

@Composable
fun Body(
    navController: () -> NavHostController,
    theState: () -> State<ViewAllProductsState>,
    onEvent: (ViewAllProductsEvent) -> Unit,
){

    val state by theState()

    val onClickFavorites = remember {
        { product: ProductDetails ->
            if (product.isFavorite) {
                onEvent(ViewAllProductsEvent.RemoveFavorite(product))
            } else {
                onEvent(ViewAllProductsEvent.AddFavorite(product))
            }
        }
    }

    val listNotLoading = remember {
        derivedStateOf {
            state.requestState != RequestState.LOADING
        }
    }

    val refresher = rememberLoadableListState(
        onLoadMore = { me ->
            onEvent(ViewAllProductsEvent.LoadPopularProducts(me))
        },
        onRefresh = { me ->
            onEvent(ViewAllProductsEvent.Refresh(me))
        },
    )

    LaunchedEffect(listNotLoading.value){
        refresher.enableLoading.value = listNotLoading.value
    }


    SetState(
        requestState = state.requestState,
        onSuccess = {
            LazyVerticalGridLoadable(

                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                state = refresher
            ) {
                items(state.products.size) { index ->
                    ProductCardOne({ state.products[index] },navController, onClickFavorites)
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
                    state.errorMessage!!,
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
                            onEvent(ViewAllProductsEvent.Init)
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ){
                items(7){
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .shimmer()
                            .background(
                                Scheme.onPrimary.copy(alpha = 0.04f)
                            ).height(220.dp)
                    )
                }
            }
        }
    )
}