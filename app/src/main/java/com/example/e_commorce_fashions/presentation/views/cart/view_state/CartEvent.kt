package com.example.e_commorce_fashions.presentation.views.cart.view_state

import com.example.e_commorce_fashions.domain.models.CartItemDetails

sealed interface CartEvent {
    data class RemoveItem(val item: CartItemDetails): CartEvent
    data class IncreaseQuantity(val item: CartItemDetails): CartEvent
    data class DecreaseQuantity(val item: CartItemDetails): CartEvent
    data object ReloadPage: CartEvent
}