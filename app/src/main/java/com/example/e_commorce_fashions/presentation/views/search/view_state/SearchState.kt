package com.example.e_commorce_fashions.presentation.views.search.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.CategoryDetails
import com.example.e_commorce_fashions.domain.models.ProductDetails

data class SearchState(
    val requestState: RequestState = RequestState.IDLE,
    val categories: List<CategoryDetails> = emptyList(),
    val products: List<ProductDetails> = emptyList(),
    val errorMessage: String = "",
)