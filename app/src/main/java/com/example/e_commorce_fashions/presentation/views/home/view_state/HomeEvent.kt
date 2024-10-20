package com.example.e_commorce_fashions.presentation.views.home.view_state

import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LoadableListState

sealed interface HomeEvent{
    data class LoadNewArrivalsProducts(val loadState: LoadableListState): HomeEvent
    data class LoadPopularProducts(val loadState: LoadableListState): HomeEvent
    data class AddFavorite(val productDetails: ProductDetails): HomeEvent
    data class RemoveFavorite(val productDetails: ProductDetails): HomeEvent
    data object ReloadPage: HomeEvent
}