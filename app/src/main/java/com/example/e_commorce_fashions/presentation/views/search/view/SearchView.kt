package com.example.e_commorce_fashions.presentation.views.search.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LazyVerticalGridLoadable
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.common.TextFormField
import com.example.e_commorce_fashions.presentation.common.rememberLoadableListState
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ProductCardOne
import com.example.e_commorce_fashions.presentation.views.search.view_state.SearchEvent
import com.example.e_commorce_fashions.presentation.views.search.view_state.SearchViewState
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsEvent
import kotlinx.coroutines.launch
import me.vponomarenko.compose.shimmer.shimmer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    navController: () -> NavHostController,
    searchViewState: () -> SearchViewState
) {
    val uiState = searchViewState()
    val state = uiState.state.collectAsStateWithLifecycle()

    val searchText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val isWriting = remember { derivedStateOf { searchText.value.trim().isNotEmpty() } }
    val isSearchFilterOpened = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf(-1) }
    val maxPrice = 700f
    val sliderRange = remember { mutableStateOf(1f..maxPrice) }

    val onClickBack: State<() -> Unit> = rememberUpdatedState {
        run {
            scope.launch {
                navController().navigateUp()
            }
        }
    }

    val onFilterClick = remember(isSearchFilterOpened.value) {
        {
            isSearchFilterOpened.value = !isSearchFilterOpened.value
            if (isSearchFilterOpened.value) {
                uiState.onEvent(SearchEvent.GetCategories)
            }
        }
    }

    val onCategorySelected: State<(Int) -> Unit> = rememberUpdatedState {
        run {
            selectedCategory.value = it
        }
    }

    val onClickFavorites = remember {
        { product: ProductDetails ->
            if (product.isFavorite) {
                uiState.onEvent(SearchEvent.RemoveFavorite(product))
            } else {
                uiState.onEvent(SearchEvent.AddFavorite(product))
            }
        }
    }

    val listNotLoading = remember {
        derivedStateOf {
            state.value.requestState != RequestState.LOADING
        }
    }

    val refresher = rememberLoadableListState(
        onLoadMore = { me ->
            uiState.onEvent(SearchEvent.LoadMore)
        },
        onRefresh = { me ->
            uiState.onEvent(SearchEvent.RefreshPage)
        },
    )

    LaunchedEffect(listNotLoading.value){
        refresher.enableLoading.value = listNotLoading.value
    }

    Scaffold(
        containerColor = Scheme.primary,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AnimatedContent(targetState = isWriting.value, label = "search-bar",
                    transitionSpec = {
                        (slideInHorizontally(
                            animationSpec = tween(150),
                        ) + fadeIn(tween(150))).togetherWith(
                            slideOutHorizontally(tween(150)) + fadeOut(tween(150))
                        )
                    }) { isWriting_ ->
                    Box(Modifier.padding(start = 20.dp)) {
                        if (isWriting_) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        uiState.onEvent(
                                            SearchEvent.SearchProduct(
                                                searchText.value,
                                                sliderRange.value.start.toInt(),
                                                sliderRange.value.endInclusive.toInt(),
                                                selectedCategory.value,
                                                refresher
                                            )
                                        )
                                    }
                                    .background(Scheme.onPrimary)
                                    .size(50.dp)
                                    .padding(15.dp)
                                    .wrapContentSize(Alignment.Center),
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        R.drawable.ic_search,
                                        contentScale = ContentScale.Crop
                                    ),
                                    contentDescription = "menu",
                                    colorFilter = ColorFilter.tint(Scheme.primary)
                                )
                            }
                        } else {
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
                        }
                    }
                }

                TextField(
                    modifier = Modifier.weight(1f),
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    shape = RoundedCornerShape(99999.dp),
                    singleLine = true,
                    textStyle = TitleLarge.copy(
                        color = Scheme.onPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary.copy(.4f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Scheme.onPrimary,
                        focusedContainerColor = Scheme.onPrimary.copy(.06f),
                        unfocusedContainerColor = Scheme.onPrimary.copy(.06f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                        )
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onFilterClick()
                        }
                        .background(Scheme.onPrimary)
                        .requiredSize(40.dp)
                        .padding(12.dp)
                        .wrapContentSize(Alignment.Center),
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            R.drawable.is_filter,
                            contentScale = ContentScale.Crop
                        ),
                        contentDescription = "menu",
                        colorFilter = ColorFilter.tint(Scheme.primary)
                    )
                }
            }

            AnimatedVisibility(
                isSearchFilterOpened.value,
                enter = expandVertically(tween(350)) + fadeIn(tween(350)),
                exit = shrinkVertically(tween(350)) + fadeOut(tween(350))
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.categories),
                        style = TitleLarge.copy(
                            color = Scheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {

                        CategoryCard(
                            label = stringResource(R.string.all),
                            index = -1,
                            selected = selectedCategory.value == -1,
                            onClick = onCategorySelected.value
                        )

                        for (catIndex in 0..<state.value.categories.size) {
                            CategoryCard(
                                label = state.value.categories[catIndex].name,
                                index = catIndex,
                                selected = selectedCategory.value == catIndex,
                                onClick = onCategorySelected.value
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.price_range),
                        style = TitleLarge.copy(
                            color = Scheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    )

                    Column {

                        RangeSlider(
                            value = sliderRange.value,
                            valueRange = 1f..maxPrice,
                            steps = maxPrice.toInt(),
                            onValueChange = {
                                if (it.endInclusive - it.start >= 20f) {
                                    sliderRange.value = it
                                }
                            },
                            startThumb = { _ ->
                                val color = Scheme.onPrimary

                                Box(
                                    modifier = Modifier
                                        .requiredSize(14.dp)
                                        .background(Scheme.primary, shape = CircleShape)
                                        .border(1.8.dp, color, shape = CircleShape)
                                        .wrapContentSize(Alignment.Center)
                                )

                            },
                            endThumb = { _ ->
                                val color = Scheme.onPrimary
                                Box(
                                    modifier = Modifier
                                        .requiredSize(14.dp)
                                        .background(Scheme.primary, shape = CircleShape)
                                        .border(1.8.dp, color, shape = CircleShape)
                                        .wrapContentSize(Alignment.Center)
                                )
                            },
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color.Transparent,
                                activeTickColor = Scheme.onPrimary,
                                disabledInactiveTickColor = Scheme.onPrimary.copy(.1f),
                                thumbColor = Scheme.onPrimary,
                                inactiveTrackColor = Color.Transparent,
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "\$${sliderRange.value.start.toInt()}",
                                style = TitleLarge.copy(
                                    color = Scheme.onPrimary.copy(.4f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                )
                            )

                            Text(
                                text = "\$${sliderRange.value.endInclusive.toInt()}",
                                style = TitleLarge.copy(
                                    color = Scheme.onPrimary.copy(.4f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Scheme.onPrimary)
                            .clickable {
                                uiState.onEvent(
                                    SearchEvent.SearchProduct(
                                        searchText.value,
                                        sliderRange.value.start.toInt(),
                                        sliderRange.value.endInclusive.toInt(),
                                        selectedCategory.value,
                                        refresher
                                    )
                                )
                            }
                            .padding(vertical = 5.dp, horizontal = 15.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.apply),
                            style = TitleLarge.copy(
                                color = Scheme.primary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                            )
                        )
                    }

                }
            }
            Spacer(
                modifier = Modifier
                    .height(20.dp)
            )

            SetState(
                requestState = state.value.requestState,
                onSuccess = {
                    LazyVerticalGridLoadable(

                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        state = refresher
                    ) {
                        items(state.value.products.size) { index ->
                            ProductCardOne({ state.value.products[index] },navController, onClickFavorites)
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
                                    uiState.onEvent(
                                        SearchEvent.SearchProduct(
                                            searchText.value,
                                            sliderRange.value.start.toInt(),
                                            sliderRange.value.endInclusive.toInt(),
                                            selectedCategory.value,
                                            refresher
                                        )
                                    )
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


@Composable
fun CategoryCard(
    label: String,
    index: Int,
    selected: Boolean = false,
    onClick: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) Scheme.onPrimary else Scheme.primary)
            .border(
                width = 1.3.dp,
                color = (if (selected) Scheme.primary else Scheme.onPrimary).copy(.1f),
                shape = CircleShape
            )
            .clickable {
                onClick(index)
            }
            .padding(vertical = 5.dp, horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = TitleLarge.copy(
                color = if (selected) Scheme.primary else Scheme.onPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        )
    }
}