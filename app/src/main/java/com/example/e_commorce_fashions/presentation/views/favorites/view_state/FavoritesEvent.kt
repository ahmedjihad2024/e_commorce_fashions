package com.example.e_commorce_fashions.presentation.views.favorites.view_state

import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.presentation.common.LoadableListState

sealed interface FavoritesEvent{
    data class GetFavorites(val refresher: LoadableListState): FavoritesEvent
    data class LoadMore(val refresher: LoadableListState): FavoritesEvent
    data class RemoveFavorite(val productDetails: ProductDetails): FavoritesEvent
    data class RemoveFavoriteLocalList(val productDetails: ProductDetails): FavoritesEvent
}