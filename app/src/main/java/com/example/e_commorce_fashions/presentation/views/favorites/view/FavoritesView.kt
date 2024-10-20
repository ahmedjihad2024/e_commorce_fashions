package com.example.e_commorce_fashions.presentation.views.favorites.view

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LazyVerticalGridLoadable
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.common.rememberLoadableListState
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesEvent
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesViewState
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardOne
import me.vponomarenko.compose.shimmer.shimmer

@Composable
fun FavoritesView(
    navController: () -> NavHostController,
    viewState: () -> FavoritesViewState,
){
    val viewModel = viewState()
    val state = viewModel.state.collectAsState()

    val init = rememberSaveable {
        mutableStateOf(false)
    }

    val onClickBack: State<() -> Unit> = rememberUpdatedState {
        run {
            navController().navigateUp()
        }
    }

    val onClickFavorites = remember {
        { product: ProductDetails ->
            viewModel.onEvent(FavoritesEvent.RemoveFavorite(product))
        }
    }

    val listNotLoading = remember {
        derivedStateOf {
            state.value.requestState != RequestState.LOADING
        }
    }

    val refresher = rememberLoadableListState(
        onLoadMore = { me ->
            viewModel.onEvent(FavoritesEvent.LoadMore(me))
        },
        onRefresh = { me ->
            viewModel.onEvent(FavoritesEvent.GetFavorites(me))
        },
    )

    LaunchedEffect(listNotLoading.value){
        refresher.enableLoading.value = listNotLoading.value
    }

    LaunchedEffect(init.value){
        if(!init.value){
            Di.sharedData.value.setData("favorites-view-model", viewModel)
            viewModel.onEvent(FavoritesEvent.GetFavorites(refresher))
            init.value = true
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Scheme.primary,
    ) { scaffoldPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onClickBack.value()
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
                        contentDescription = "menu",
                        colorFilter = ColorFilter.tint(Scheme.primary)
                    )
                }
                Text(
                    stringResource(R.string.my_favourites),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Scheme.onPrimary,
                    )
                )
            }

            SetState(
                requestState = state.value.requestState,
                onSuccess = {
                    LazyVerticalGridLoadable(
                        contentPadding = PaddingValues(vertical = 20.dp),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        state = refresher
                    ) {
                        items(state.value.items.size) { index ->
                            ProductCardOne({ state.value.items[index] },navController, onClickFavorites)
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
                            state.value.errorMessage,
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
                                    viewModel.onEvent(FavoritesEvent.GetFavorites(refresher))
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
                        contentPadding = PaddingValues(vertical = 20.dp),
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
                                    )
                                    .height(220.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}