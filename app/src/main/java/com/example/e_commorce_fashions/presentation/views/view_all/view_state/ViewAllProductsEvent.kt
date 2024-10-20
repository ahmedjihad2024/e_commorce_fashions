package com.example.e_commorce_fashions.presentation.views.view_all.view_state

import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LoadableListState

sealed interface ViewAllProductsEvent {
    data class LoadPopularProducts(val loadState: LoadableListState): ViewAllProductsEvent
    data class AddFavorite(val productDetails: ProductDetails): ViewAllProductsEvent
    data class RemoveFavorite(val productDetails: ProductDetails): ViewAllProductsEvent
    data object Init: ViewAllProductsEvent
    data class Refresh(val loadState: LoadableListState): ViewAllProductsEvent
}