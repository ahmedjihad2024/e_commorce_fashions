package com.example.e_commorce_fashions.presentation.views.my_orders.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.OrderDetails

data class MyOrdersState(
    val requestState: RequestState = RequestState.LOADING,
    val errorMessage: String? = null,
    val orders: List<OrderDetails> = emptyList()
)