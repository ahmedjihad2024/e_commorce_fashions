package com.example.e_commorce_fashions.presentation.views.search.view_state

import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsEvent

sealed interface SearchEvent {
    data object RefreshPage : SearchEvent
    data object LoadMore : SearchEvent
    data class AddFavorite(val productDetails: ProductDetails) : SearchEvent
    data class RemoveFavorite(val productDetails: ProductDetails) : SearchEvent
    data class SearchProduct(
        val query: String,
        val minPrice: Int,
        val maxPrice: Int,
        val categoryIndex: Int,
        val loadState: LoadableListState
    ) : SearchEvent

    data object GetCategories : SearchEvent
}