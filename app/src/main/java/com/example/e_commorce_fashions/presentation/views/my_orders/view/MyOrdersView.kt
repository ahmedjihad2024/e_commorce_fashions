package com.example.e_commorce_fashions.presentation.views.my_orders.view

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.presentation.common.ColumnLoadable
import com.example.e_commorce_fashions.presentation.common.LazyVerticalGridLoadable
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.common.rememberLoadableListState
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardOne
import com.example.e_commorce_fashions.presentation.views.my_orders.view_state.MyOrdersEvent
import com.example.e_commorce_fashions.presentation.views.my_orders.view_state.MyOrdersViewState
import com.example.e_commorce_fashions.presentation.views.my_orders.view_state.MyOrdersViewStateFactory
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsEvent
import me.vponomarenko.compose.shimmer.shimmer
import java.util.Locale

@Composable
fun MyOrdersView(
    navController: () -> NavHostController,
) {

    val uiState = viewModel<MyOrdersViewState>(factory = MyOrdersViewStateFactory())
    val state by uiState.state.collectAsState()

    val loadOrders = rememberSaveable {
        mutableStateOf(false)
    }

    val onClickBack: State<() -> Unit> = rememberUpdatedState {
        run {
            navController().navigateUp()
        }
    }

    val listNotLoading = remember {
        derivedStateOf {
            state.requestState != RequestState.LOADING
        }
    }

    val refresher = rememberLoadableListState(
        onLoadMore = { me ->
            uiState.onEvent(MyOrdersEvent.LoadMoreOrders(me))
        },
        onRefresh = { me ->
            uiState.onEvent(MyOrdersEvent.LoadOrders(me))
        },
    )

    LaunchedEffect(listNotLoading.value) {
        refresher.enableLoading.value = listNotLoading.value
    }

    LaunchedEffect(loadOrders.value){
        if(!loadOrders.value){
            uiState.onEvent(MyOrdersEvent.LoadOrders(refresher))
            loadOrders.value = true
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
                .padding(horizontal = 20.dp).padding(top = 20.dp),
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
                    stringResource(R.string.my_orders),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Scheme.onPrimary,
                    )
                )
            }

            SetState(
                requestState = state.requestState,
                onSuccess = {
                    ColumnLoadable(
                        contentPadding = PaddingValues(
                            top = 20.dp,
                            bottom = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        state = refresher,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.orders) { item ->
                            OrderCard(
                                status = item.status.name,
                                paymentType = item.paymentMethod.name.replace("_", " "),
                                totalPrice = item.totalPrice
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
                                    uiState.onEvent(MyOrdersEvent.LoadOrders(refresher))
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
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        repeat(10) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .shimmer()
                                    .background(
                                        Scheme.onPrimary.copy(alpha = 0.04f)
                                    )
                                    .height(100.dp).fillMaxWidth()
                            )
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun OrderCard(status: String, paymentType: String, totalPrice: Double) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 5.dp,
                ambientColor = Scheme.onPrimary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .fillMaxWidth()
            .background(Scheme.primary)
            .padding(5.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = status,
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Scheme.onPrimary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = paymentType,
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Scheme.onPrimary,
                        fontSize = 14.sp
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // Add some space between the rows
                Text(
                    text = String.format(Locale.getDefault(), "%.2f", totalPrice),
                    style = TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Scheme.onPrimary,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}