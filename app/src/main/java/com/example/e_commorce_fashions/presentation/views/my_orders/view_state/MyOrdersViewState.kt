package com.example.e_commorce_fashions.presentation.views.my_orders.view_state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.GetOrdersUseCase
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyOrdersViewStateFactory(
    val repository: Repository = Di.repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyOrdersViewState(repository) as T
    }
}

class MyOrdersViewState(
    repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow(MyOrdersState())
    val state = _state.asStateFlow()

    private val _getOrders = GetOrdersUseCase(repository)

    private var _lastKey: String? = null

    fun onEvent(
        event: MyOrdersEvent
    ) {
        when (event) {
            is MyOrdersEvent.LoadOrders -> {
                _getOrders(
                    refresh = true,
                    refresher = event.refresher
                )
            }

            is MyOrdersEvent.LoadMoreOrders -> {
                _getOrders(refresher = event.refresher)
            }
        }
    }


    private fun _getOrders(
        refresh: Boolean = false,
        refresher: LoadableListState? = null
    ) {
        viewModelScope.launch {
            if(refresh){
                _lastKey = null
                _state.update {
                    it.copy(
                        requestState = RequestState.LOADING
                    )
                }
            }else{
                refresher?.setLoadingMore()
            }

            val result = _getOrders.execute(OrderReq(_lastKey, 10))

            result.fold(
                left = { left ->
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                },
                right = { right ->
                    val key = right.orders.lastOrNull()?.key
                    _lastKey = key ?: _lastKey

                    if(refresh){
                        refresher?.setNotRefreshing()
                        if (key == null) {
                            refresher?.setLoadNoMore()
                        }
                        _state.update {
                            it.copy(
                                requestState = RequestState.SUCCESS,
                                orders = right.orders
                            )
                        }
                    }else {
                        if (key == null) {
                            refresher?.setLoadNoMore()
                        } else {
                            refresher?.setLoadingComplete()
                        }
                        _state.update {
                            it.copy(
                                requestState = RequestState.SUCCESS,
                                orders = it.orders + right.orders
                            )
                        }
                    }

                }
            )

        }
    }

}