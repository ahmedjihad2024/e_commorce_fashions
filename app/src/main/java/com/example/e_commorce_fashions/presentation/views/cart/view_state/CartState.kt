package com.example.e_commorce_fashions.presentation.views.cart.view_state

import androidx.compose.runtime.Immutable
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.CartItemDetails

@Immutable
data class UiCartState (
    val requestState: RequestState = RequestState.EMPTY,
    val errorMessage: String? = null,
    val cartItems: MutableSet<CartItemDetails> = mutableSetOf(),
    val total: Double = 0.0,
    val itemCount: Int = 0
)