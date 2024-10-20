package com.example.e_commorce_fashions.presentation.views.view_all.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails

data class ViewAllProductsState(
    val requestState: RequestState = RequestState.LOADING,
    val errorMessage: String? = null,
    val products: List<ProductDetails> = emptyList(),
    )