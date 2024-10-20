package com.example.e_commorce_fashions.presentation.views.favorites.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails

data class FavoritesState(
    val items: List<ProductDetails> = emptyList(),
    val requestState: RequestState = RequestState.LOADING,
    val errorMessage: String = ""
)