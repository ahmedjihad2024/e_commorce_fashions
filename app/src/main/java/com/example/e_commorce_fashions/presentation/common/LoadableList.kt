package com.example.e_commorce_fashions.presentation.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Intrinsics

enum class LOADING_STATE {
    LOADING_MORE,
    LOADING_COMPLETE,
    ERROR,
    NO_MORE,
    REFRESHING,
    REFRESHING_COMPLETE,
    IDLE
}

class LoadableListState(
    val onLoadMore: (suspend (LoadableListState) -> Unit)? = null,
    val onRefresh: (suspend (LoadableListState) -> Unit)? = null,

    ) {
    var loadState by mutableStateOf(LOADING_STATE.IDLE)
    val enableLoading: MutableState<Boolean> = mutableStateOf(true)

    fun setLoadingState(loadingState: LOADING_STATE) {
        loadState = loadingState
    }

    fun setLoadingComplete() {
        loadState = LOADING_STATE.LOADING_COMPLETE
    }

    fun setLoadingMore() {
        loadState = LOADING_STATE.LOADING_MORE
    }

    fun setLoadIdle() {
        loadState = LOADING_STATE.IDLE
    }

    fun setLoadError() {
        loadState = LOADING_STATE.ERROR
    }

    fun setLoadNoMore() {
        loadState = LOADING_STATE.NO_MORE
    }

    fun reset() {
        loadState = LOADING_STATE.IDLE
    }

    fun setRefreshing() {
        loadState = LOADING_STATE.REFRESHING
    }

    fun setNotRefreshing() {
        loadState = LOADING_STATE.REFRESHING_COMPLETE
    }

}

@Composable
fun rememberLoadableListState(
    onLoadMore: (suspend (LoadableListState) -> Unit)? = null,
    onRefresh: (suspend (LoadableListState) -> Unit)? = null,
): LoadableListState {
    return remember() { LoadableListState(onLoadMore, onRefresh) }
}


@Composable
fun RowLoadable(
    modifier: Modifier = Modifier,
    state: LoadableListState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onLoadMoreLoading: (@Composable () -> Unit)? = null,
    onLoadMoreIdle: (@Composable () -> Unit)? = null,
    onLoadMoreError: (@Composable () -> Unit)? = null,
    onLoadNoMore: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val scrollState = rememberLazyListState(0)
    val scope = rememberCoroutineScope()


    val nested = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (
                    scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == scrollState.layoutInfo.totalItemsCount - 1 &&
                    state.loadState != LOADING_STATE.LOADING_MORE &&
                    state.loadState != LOADING_STATE.NO_MORE &&
                    state.enableLoading.value
                ) {
                    state.loadState = LOADING_STATE.LOADING_MORE
                    scope.launch {
                        state.onLoadMore?.invoke(state)
                    }
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LazyRow(
        state = scrollState,
        contentPadding = contentPadding,
        modifier = Modifier
            .nestedScroll(nested)
            .then(modifier),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {

        content()

        item {
            AnimatedContent(
                state.loadState,
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentSize(Alignment.Center),
                transitionSpec = {
                    if (targetState == LOADING_STATE.LOADING_MORE) {
                        // Content is entering (success state)
                        (slideInHorizontally { width -> width / 3 } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width / 3 } + fadeOut()
                        )
                    } else {
                        // Content is exiting (loading state)
                        (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width / 3 } + fadeOut()
                        )
                    }
                }, label = "Load more items"
            ) {
                when (it) {
                    LOADING_STATE.LOADING_MORE -> {
                        onLoadMoreLoading?.invoke() ?: Box(
                            modifier = Modifier
                                .padding(30.dp)
                                .size(40.dp)
                                .wrapContentSize(Alignment.Center),
                        ) {
                            CircularProgressIndicator(
                                color = Scheme.onPrimary,
                                modifier = Modifier
                                    .size(25.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        LaunchedEffect(it) {
                            scope.launch {
                                delay(200)
                                scrollState.animateScrollToItem(
                                    scrollState.layoutInfo.totalItemsCount,
                                )
                            }
                        }
                    }

                    LOADING_STATE.NO_MORE -> {
                        onLoadNoMore?.invoke() ?: Box {}
                    }

                    LOADING_STATE.ERROR -> {
                        onLoadMoreError?.invoke() ?: Box {}
                    }

                    LOADING_STATE.IDLE -> {
                        onLoadMoreIdle?.invoke() ?: Box {}
                    }

                    else -> Box {}
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnLoadable(
    modifier: Modifier = Modifier,
    state: LoadableListState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onLoadMoreLoading: (@Composable () -> Unit)? = null,
    onLoadMoreIdle: (@Composable () -> Unit)? = null,
    onLoadMoreError: (@Composable () -> Unit)? = null,
    onLoadNoMore: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val scrollState = rememberLazyListState(0)
    val scope = rememberCoroutineScope()
    val refresher = remember { PullToRefreshState() }


    val nested = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (
                    scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == scrollState.layoutInfo.totalItemsCount - 1 &&
                    state.loadState != LOADING_STATE.LOADING_MORE &&
                    state.loadState != LOADING_STATE.NO_MORE &&
                    state.enableLoading.value
                ) {
                    state.loadState = LOADING_STATE.LOADING_MORE
                    scope.launch {
                        state.onLoadMore?.invoke(state)
                    }
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    PullToRefreshBox(
        state.loadState == LOADING_STATE.REFRESHING,
        {
            state.setRefreshing()
            scope.launch {
                state.onRefresh?.invoke(state)
            }
        },
        state = refresher,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .nestedScroll(nested)
                .then(modifier),
            contentPadding = contentPadding,
            state = scrollState,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {

            content()

            item {
                AnimatedContent(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    targetState = state.loadState,
                    transitionSpec = {
                        if (targetState == LOADING_STATE.LOADING_MORE) {
                            (slideInVertically { height -> height / 3 } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height / 3 } + fadeOut()
                            )
                        } else {
                            (slideInVertically { height -> -height / 3 } + fadeIn()).togetherWith(
                                slideOutVertically { height -> height / 3 } + fadeOut()
                            )
                        }
                    }, label = "Load more items"
                ) {
                    when (it) {
                        LOADING_STATE.LOADING_MORE -> {
                            onLoadMoreLoading?.invoke() ?: Box(
                                modifier = Modifier
                                    .padding(30.dp)
                                    .size(40.dp)
                                    .wrapContentSize(Alignment.Center),
                            ) {
                                CircularProgressIndicator(
                                    color = Scheme.onPrimary,
                                    modifier = Modifier
                                        .size(25.dp),
                                    strokeWidth = 2.dp
                                )
                            }

                            LaunchedEffect(it) {
                                scope.launch {
                                    delay(200)
                                    scrollState.animateScrollToItem(
                                        scrollState.layoutInfo.totalItemsCount,
                                    )
                                }
                            }
                        }

                        LOADING_STATE.NO_MORE -> {
                            onLoadNoMore?.invoke() ?: Box {}
                        }

                        LOADING_STATE.ERROR -> {
                            onLoadMoreError?.invoke() ?: Box {}
                        }

                        LOADING_STATE.IDLE -> {
                            onLoadMoreIdle?.invoke() ?: Box {}
                        }

                        else -> Box {}
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyVerticalGridLoadable(
    modifier: Modifier = Modifier,
    columns: GridCells,
    state: LoadableListState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement. Vertical = if (!reverseLayout) Arrangement. Top else Arrangement. Bottom,
    horizontalArrangement: Arrangement. Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    onLoadMoreLoading: (@Composable () -> Unit)? = null,
    onLoadMoreIdle: (@Composable () -> Unit)? = null,
    onLoadMoreError: (@Composable () -> Unit)? = null,
    onLoadNoMore: (@Composable () -> Unit)? = null,
    content:  LazyGridScope.() -> Unit
) {
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val refresher = remember { PullToRefreshState() }


    val nested = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (
                    scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == scrollState.layoutInfo.totalItemsCount - 1 &&
                    state.loadState != LOADING_STATE.LOADING_MORE &&
                    state.loadState != LOADING_STATE.NO_MORE &&
                    state.enableLoading.value
                ) {
                    state.loadState = LOADING_STATE.LOADING_MORE
                    scope.launch {
                        state.onLoadMore?.invoke(state)
                    }
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    PullToRefreshBox(
        state.loadState == LOADING_STATE.REFRESHING,
        {
            state.setRefreshing()
            scope.launch {
                state.onRefresh?.invoke(state)
            }
        },
        state = refresher,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid (
            modifier = Modifier
                .nestedScroll(nested)
                .then(modifier),
            contentPadding = contentPadding,
            state = scrollState,
            columns = columns,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement
        ) {

            content()

            item {
                AnimatedContent(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    targetState = state.loadState,
                    transitionSpec = {
                        if (targetState == LOADING_STATE.LOADING_MORE) {
                            (slideInVertically { height -> height / 3 } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height / 3 } + fadeOut()
                            )
                        } else {
                            (slideInVertically { height -> -height / 3 } + fadeIn()).togetherWith(
                                slideOutVertically { height -> height / 3 } + fadeOut()
                            )
                        }
                    }, label = "Load more items"
                ) {
                    when (it) {
                        LOADING_STATE.LOADING_MORE -> {
                            onLoadMoreLoading?.invoke() ?: Box(
                                modifier = Modifier
                                    .padding(30.dp)
                                    .size(40.dp)
                                    .wrapContentSize(Alignment.Center),
                            ) {
                                CircularProgressIndicator(
                                    color = Scheme.onPrimary,
                                    modifier = Modifier
                                        .size(25.dp),
                                    strokeWidth = 2.dp
                                )
                            }

                            LaunchedEffect(it) {
                                scope.launch {
                                    delay(200)
                                    scrollState.animateScrollToItem(
                                        scrollState.layoutInfo.totalItemsCount,
                                    )
                                }
                            }
                        }

                        LOADING_STATE.NO_MORE -> {
                            onLoadNoMore?.invoke() ?: Box {}
                        }

                        LOADING_STATE.ERROR -> {
                            onLoadMoreError?.invoke() ?: Box {}
                        }

                        LOADING_STATE.IDLE -> {
                            onLoadMoreIdle?.invoke() ?: Box {}
                        }

                        else -> Box {}
                    }

                }
            }
        }
    }
}


