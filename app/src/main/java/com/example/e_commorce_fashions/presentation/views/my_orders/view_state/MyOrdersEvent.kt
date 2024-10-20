package com.example.e_commorce_fashions.presentation.views.my_orders.view_state

import com.example.e_commorce_fashions.presentation.common.LoadableListState

sealed interface MyOrdersEvent{
    data class LoadOrders(val refresher: LoadableListState): MyOrdersEvent
    data class LoadMoreOrders(val refresher: LoadableListState): MyOrdersEvent
}