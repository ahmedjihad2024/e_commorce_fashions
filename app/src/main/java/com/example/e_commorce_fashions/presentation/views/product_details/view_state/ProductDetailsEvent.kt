package com.example.e_commorce_fashions.presentation.views.product_details.view_state

import com.example.e_commorce_fashions.domain.models.ProductDetails

sealed class ProductDetailsEvent{
    data object AddFavorite : ProductDetailsEvent()
    data object RemoveFavorite : ProductDetailsEvent()
    data object AddToCart : ProductDetailsEvent()
    data object RemoveFromCart : ProductDetailsEvent()
    data object IncreaseQuantity : ProductDetailsEvent()
    data object DecreaseQuantity : ProductDetailsEvent()
    data class SelectedColor(val color: String) : ProductDetailsEvent()
    data class SelectedSize(val size: String) : ProductDetailsEvent()
}