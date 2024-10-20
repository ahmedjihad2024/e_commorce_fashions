package com.example.e_commorce_fashions.presentation.views.product_details.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails

data class ProductDetailsUiState(
    val product: ProductDetails? = null,
    val isInCart: Boolean = false,
    val quantity: Int = 1,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
)