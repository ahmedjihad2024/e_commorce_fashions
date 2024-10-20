package com.example.e_commorce_fashions.presentation.views.cart.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.domain.models.CartItemDetails
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.cart.view_state.CartEvent
import com.example.e_commorce_fashions.presentation.views.cart.view_state.CartViewState
import com.example.e_commorce_fashions.presentation.views.cart.view_state.CartViewStateFactory
import com.example.e_commorce_fashions.presentation.views.home.view_state.HomeEvent
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsEvent
import kotlinx.coroutines.launch

@Composable
fun CartView(navController: () -> NavHostController) {

    val uiState = viewModel<CartViewState>(
        factory = CartViewStateFactory()
    )

    val state = uiState.state.collectAsState()

    val processPayment: (Double) -> Unit by rememberUpdatedState {
        Di.sharedData.value.setData("amount", it)
        navController().navigate(Views.ChoiceLocationView.route)
    }

    Scaffold(
        containerColor = Scheme.primary
    ) { scaffoldPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(20.dp)
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = stringResource(id = R.string.my_cart),
                style = TitleSmall.copy(
                    color = Scheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                Modifier.weight(1f),
            ) {
                SetState(
                    requestState = state.value.requestState,
                    onSuccess = {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            LazyColumn(
                                Modifier
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                items(state.value.cartItems.toMutableList()){ item ->
                                    CartItem(
                                        navController = navController,
                                        itemDetails = { item },
                                        onEvent = uiState::onEvent
                                    )
                                }
                            }

                           Column(
                               modifier = Modifier.padding(top = 20.dp),
                               verticalArrangement = Arrangement.spacedBy(20.dp)
                           ) {
                               Row(
                                   Modifier.fillMaxWidth(),
                                   horizontalArrangement = Arrangement.SpaceBetween,
                                   verticalAlignment = Alignment.CenterVertically
                               ) {
                                   Text(
                                       text = stringResource(id = R.string.total_price, state.value.itemCount),
                                       style = TitleSmall.copy(
                                           color = Scheme.onPrimary.copy(.4f),
                                           fontSize = 16.sp,
                                           fontWeight = FontWeight.SemiBold
                                       )
                                   )

                                   Text(
                                       String.format("$%.2f", state.value.total),
                                       style = TitleSmall.copy(
                                           color = Scheme.onPrimary,
                                           fontSize = 18.sp,
                                           fontWeight = FontWeight.Bold
                                       )
                                   )
                               }

                               Row(
                                   Modifier
                                       .clip(RoundedCornerShape(15.dp))
                                       .fillMaxWidth()
                                       .clickable {
                                           processPayment(state.value.total)
                                       }
                                       .background(Scheme.onPrimary)
                                       .padding(10.dp),
                                   horizontalArrangement = Arrangement.SpaceBetween,
                                   verticalAlignment = Alignment.CenterVertically
                               ){
                                   Text(
                                       text = stringResource(id = R.string.proceed_to_checkout),
                                       style = TitleSmall.copy(
                                           color = Scheme.primary,
                                           fontSize = 14.sp,
                                           fontWeight = FontWeight.SemiBold
                                       )
                                   )
                                   Box(
                                       Modifier.clip(RoundedCornerShape(13.dp))
                                       .background(Scheme.primary)
                                           .padding(10.dp)
                                   ){
                                       Image(
                                           painter = rememberAsyncImagePainter(
                                               R.drawable.ic_arrow_back,
                                               contentScale = ContentScale.Crop
                                           ),
                                           contentDescription = "menu", colorFilter = ColorFilter.tint(Scheme.onPrimary),
                                           modifier = Modifier.size(20.dp).graphicsLayer {
                                               rotationZ = 180f
                                           }

                                       )
                                   }
                               }
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
                                state.value.errorMessage!!,
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
                                        uiState.onEvent(CartEvent.ReloadPage)
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
                    onEmpty = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_item_in_cart),
                                style = TitleSmall.copy(
                                    color = Scheme.onPrimary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CartItem(
    navController: () -> NavHostController,
    itemDetails: () -> CartItemDetails,
    onEvent: (CartEvent) -> Unit
) {

    val item = itemDetails()

    val scope = rememberCoroutineScope()
    val productDetailsView : ()-> Unit = remember(item) {
        {
            if(item.productDetails != null){
                scope.launch {
                    Di.sharedData.value.setData(
                        "product-details",
                        item.productDetails
                    )
                    navController().navigate(Views.ProductDetailsView.route)
                }
            }
        }
    }

    Column(
        Modifier.clip(RoundedCornerShape(15.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Box {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.Black.copy(.05f))
                    .clickable {
                        productDetailsView()
                    }
                    .fillMaxWidth()
                    .height(110.dp),
                painter = rememberAsyncImagePainter(
                    item.productDetails?.imageUrls?.first(),
                    contentScale = ContentScale.FillWidth
                ),
                contentScale = ContentScale.FillWidth,
                contentDescription = "product"
            )

            Icon(
                Icons.Rounded.Delete,
                contentDescription = "delete",
                tint = Scheme.primary,
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .clickable {
                        onEvent(CartEvent.RemoveItem(item))
                    }
                    .background(Scheme.onPrimary)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(17.dp)
            )

            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(Scheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "–",
                    modifier = Modifier
                        .clickable {
                            if(item.quantity!! > 1) onEvent(CartEvent.DecreaseQuantity(item))
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                )
                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 10.dp),
                    softWrap = false,
                    maxLines = 1,
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = "+",
                    modifier = Modifier
                        .clickable {
                            onEvent(CartEvent.IncreaseQuantity(item))
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                )
            }


        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text(
                text = item.productDetails?.name ?: "",
                style = TitleSmall.copy(
                    color = Scheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,)
            )
            Text(
                text = "\$${item.productDetails?.price}",
                style = TitleSmall.copy(
                    color = Scheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,)
            )
        }
    }
}

