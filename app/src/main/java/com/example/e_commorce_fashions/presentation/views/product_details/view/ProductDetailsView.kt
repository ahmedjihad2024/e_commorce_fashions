package com.example.e_commorce_fashions.presentation.views.product_details.view

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.presentation.common.ResizedText
import com.example.e_commorce_fashions.presentation.common.RoundedCornerShape1
import com.example.e_commorce_fashions.presentation.common.sw
import com.example.e_commorce_fashions.presentation.resources.config.theme.GoldYellow
import com.example.e_commorce_fashions.presentation.resources.config.theme.HeavyGreen
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsEvent
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsViewState
import kotlinx.coroutines.launch


@Composable
fun ProductDetailsView(navController: () -> NavHostController, uiState: ProductDetailsViewState) {

    val state by uiState.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val displayedToast = remember { mutableStateOf<Toast?>(null) }

    val onClickBack: State<() -> Unit> = rememberUpdatedState {
        run {
            scope.launch {
                navController().navigateUp()
            }
        }
    }

    LaunchedEffect(uiState.stateToast){
        uiState.stateToast.collect{
            displayedToast.value?.cancel()
            displayedToast.value = Toast.makeText(context, it, Toast.LENGTH_SHORT)
            displayedToast.value?.show()
        }
    }

    val onClickFavorites: State<() -> Unit> = rememberUpdatedState {
        run {
            if (state.product!!.isFavorite) {
                uiState.onEvent(ProductDetailsEvent.RemoveFavorite)
            } else {
                uiState.onEvent(ProductDetailsEvent.AddFavorite)
            }
        }
    }



    Scaffold(
        containerColor = Scheme.primary,
        modifier = Modifier.fillMaxSize()
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {

            val imagesCount = remember { derivedStateOf { state.product?.imageUrls?.size ?: 1 } }
            val pagerState = rememberPagerState { imagesCount.value }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                        contentDescription = "menu", colorFilter = ColorFilter.tint(Scheme.primary)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                        }
                        .background(Scheme.onPrimary.copy(.05f))
                        .size(35.dp)
                        .padding(10.dp)
                        .wrapContentSize(Alignment.Center),
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            R.drawable.ic_bag,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = "menu",
                        colorFilter = ColorFilter.tint(Scheme.onPrimary)
                    )
                }

            }

            Spacer(Modifier.height(10.dp))
            Box {
                HorizontalPager(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(
                            RoundedCornerShape1(
                                cornerRadius = 15.dp,
                                width = 100.dp,
                                height = 70.dp
                            )
                        )
                        .background(Scheme.onPrimary.copy(.1f))
                        .height(.93.sw),
                    state = pagerState,
                    beyondViewportPageCount = imagesCount.value
                ) {
                    if (
                        state.product?.imageUrls?.isNotEmpty() != null
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberAsyncImagePainter(
                                state.product!!.imageUrls!![it],
                                contentScale = ContentScale.FillBounds
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = "product"
                        )
                    }
                }
                // reviews
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 17.dp,
                                topEnd = 15.dp,
                                bottomEnd = 15.dp,
                                bottomStart = 15.dp
                            )
                        )
                        .background(Scheme.onPrimary)
                        .width(90.dp)
                        .height(60.dp)
                        .padding(5.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Rounded.Star,
                                null,
                                tint = GoldYellow,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                modifier = Modifier.widthIn(max = 150.dp),
                                text = "${state.product!!.rating}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = Scheme.primary
                                ),
                            )
                        }
                        Text(
                            modifier = Modifier.widthIn(max = 150.dp),
                            text = "${state.product!!.reviews} ${stringResource(R.string.reviews)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TitleSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = Scheme.primary
                            ),
                        )
                    }
                }

                // page indicator
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(20.dp)
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    repeat(imagesCount.value) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .graphicsLayer {
                                    alpha = if (pagerState.currentPage == it) 1f else .5f
                                }
                                .background(Scheme.primary)
                                .size(8.dp)
                        )

                    }
                }

            }

            // body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.product!!.name,
                        style = TitleSmall.copy(
                            color = Scheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    )

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Scheme.onPrimary.copy(.1f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "–",
                            modifier = Modifier
                                .clickable {
                                    if (state.quantity > 1) uiState.onEvent(ProductDetailsEvent.DecreaseQuantity)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            style = TitleSmall.copy(
                                color = Scheme.onPrimary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        )
                        Text(
                            text = state.quantity.toString(),
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
                                    uiState.onEvent(ProductDetailsEvent.IncreaseQuantity)
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

                Spacer(Modifier.height(25.dp))
                Text(
                    text = stringResource(R.string.description),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.heightIn(max = 100.dp),
                    text = state.product!!.description,
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary.copy(.5f),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                )

                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.size),
                    style = TitleSmall.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // size
                    if (state.product!!.sizes.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .weight(1f)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            state.product!!.sizes.forEachIndexed { index, size ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            uiState.onEvent(ProductDetailsEvent.SelectedSize(size))
                                        }
                                        .border(
                                            width = 1.dp,
                                            color = if (size == state.selectedSize) Color.Transparent else Scheme.onPrimary.copy(
                                                .2f
                                            ),
                                            shape = CircleShape
                                        )
                                        .background(if (size == state.selectedSize) Scheme.onPrimary else Color.Transparent)
                                        .size(40.dp)
                                        .padding(5.dp)
                                        .wrapContentSize(Alignment.Center)
                                ) {
                                    ResizedText(
                                        text = size,
                                        softWrap = false,
                                        style = TitleSmall.copy(
                                            color = if (size == state.selectedSize) Scheme.primary else Scheme.onPrimary.copy(
                                                .4f
                                            ),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                        ),
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    } else Box {}

                    Spacer(Modifier.width(10.dp))

                    // colors
                    if (state.product!!.colors.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = Scheme.onPrimary.copy(.08f),
                                    shape = CircleShape
                                )
                                .widthIn(max = 150.dp)
                                .horizontalScroll(rememberScrollState())
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),

                            ) {
                            for (color in state.product!!.colors) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            uiState.onEvent(ProductDetailsEvent.SelectedColor(color))
                                        }
                                        .border(
                                            width = 1.dp,
                                            color = Scheme.onPrimary.copy(.08f),
                                            shape = CircleShape
                                        )
                                        .size(20.dp)
                                        .background(Color(android.graphics.Color.parseColor(color)))
                                        .padding(5.dp)
                                        .wrapContentSize(Alignment.Center)
                                ) {
                                    if (color == state.selectedColor) {
                                        Icon(
                                            Icons.Rounded.Check,
                                            null,
                                            tint = Scheme.primary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // favorites button
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(13.dp)
                            )
                            .clickable {
                                onClickFavorites.value()
                            }
                            .size(55.dp)
                            .background(Scheme.onPrimary.copy(.1f))
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center)
                    ) {
                        AnimatedContent(state.product!!.isFavorite, label = "favorite-button",
                            transitionSpec = {
                                (scaleIn(
                                    initialScale = .7f,
                                    animationSpec = tween(250)
                                ) + fadeIn(animationSpec = tween(250))).togetherWith(
                                    scaleOut(
                                        targetScale = .5f,
                                        animationSpec = tween(250)
                                    ) + fadeOut(
                                        animationSpec = tween(250)
                                    )
                                )
                            }) { isIt ->
                            if (isIt) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Rounded.Favorite,
                                    contentDescription = null,
                                    tint = Scheme.onPrimary,
                                )
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        R.drawable.ic_favorites,
                                        contentScale = ContentScale.Fit
                                    ),
                                    colorFilter = ColorFilter.tint(Scheme.onPrimary),
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    // card button
                    if (state.product!!.available) {
                        CartButton(
                            onEvent = uiState::onEvent,
                            isInCart = state.isInCart,
                            quantity = state.quantity,
                            selectedSize = state.selectedSize,
                            selectedColor = state.selectedColor
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(13.dp)
                                )
                                .background(
                                    Color(
                                        0xFFF44336
                                    )
                                ).height(55.dp).padding(horizontal = 25.dp)
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(text = stringResource(R.string.product_not_available),
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Scheme.primary,
                                    fontSize = 12.sp
                                ))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CartButton(
    onEvent: (ProductDetailsEvent) -> Unit,
    isInCart: Boolean,
    quantity: Int,
    selectedSize: String? = null,
    selectedColor: String? = null,
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val showToast: (String) -> Unit = remember {
        {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val updateTransition = updateTransition(targetState = isInCart, label = "cart-button")
    val backgroundColor = updateTransition.animateColor(
        transitionSpec = { tween(200) },
        label = "background-color",
    ) { isIt ->
        if(isIt) HeavyGreen else Scheme.onPrimary
    }

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(13.dp)
            )
            .clickable {
                if (selectedSize == null) {
                    scope.launch {
                        showToast(context.getString(R.string.please_select_size))
                    }
                } else if (selectedColor == null) {
                    scope.launch {
                        showToast(context.getString(R.string.please_select_color))
                    }
                } else if(!isInCart) {
                    onEvent(
                        ProductDetailsEvent.AddToCart
                    )
                }else{
                    onEvent(
                        ProductDetailsEvent.RemoveFromCart
                    )
                }
            }
            .height(55.dp)
            .background(backgroundColor.value)
            .padding(horizontal = 25.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = rememberAsyncImagePainter(
                    R.drawable.ic_bag,
                    contentScale = ContentScale.Fit
                ),
                colorFilter = ColorFilter.tint(Scheme.primary),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )

            Text(
                text = if(isInCart) stringResource(R.string.remove_from_cart) else stringResource(R.string.add_to_cart),
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Scheme.primary,
                    fontSize = 14.sp
                ),
            )
        }
    }
}




