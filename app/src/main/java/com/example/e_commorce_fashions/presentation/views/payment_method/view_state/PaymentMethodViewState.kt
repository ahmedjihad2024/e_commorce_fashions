package com.example.e_commorce_fashions.presentation.views.payment_method.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.OrderStatus
import com.example.e_commorce_fashions.app.utils.PaymentMethod
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.EphemeralKeyReq
import com.example.e_commorce_fashions.data.requests.PaymentIntentReq
import com.example.e_commorce_fashions.data.requests.PerchesCompletedReq
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.CreatePaymentIntentUseCase
import com.example.e_commorce_fashions.domain.use_case.GetCustomerIdUseCase
import com.example.e_commorce_fashions.domain.use_case.GetEphemeralKeyUseCase
import com.example.e_commorce_fashions.domain.use_case.PerchesCompletedUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentMethodViewStateFactory(val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (
            modelClass.isAssignableFrom(PaymentMethodViewState::class.java)
        ) return PaymentMethodViewState(
            repository = repository,
        ) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PaymentMethodViewState(private val repository: Repository): ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodUiState())
    val state = _state.asStateFlow()

    private val _snackBarState = MutableSharedFlow<String>()
    val snackBarState = _snackBarState.asSharedFlow()

    private val _customerIdUseCase = GetCustomerIdUseCase(repository)
    private val _ephemeralKeyUseCase = GetEphemeralKeyUseCase(repository)
    private val _createPaymentIntentUseCase = CreatePaymentIntentUseCase(repository)
    private val _perchesCompletedUseCase = PerchesCompletedUseCase(repository)

    private var _customerId: String? = null
    private var _ephemeralKey: String? = null
    private var _clientSecret: String? = null
    val customerId: String? get() = _customerId
    val ephemeralKey: String? get() = _ephemeralKey
    val clientSecret: String? get() = _clientSecret

    fun onEvent(event: PaymentMethodEvent) {
        when (event) {
            is PaymentMethodEvent.MakePayment -> {
                _makePayment(event.amount, event.paymentMethod)
            }
            is PaymentMethodEvent.PurchaseCompleted -> {
                _pershesCompleted(event.totalPrice, event.paymentMethod, event.latitude, event.longitude)
            }
        }
    }

    private suspend fun _getCustomerId(): String? {
        return viewModelScope.async {
            val response = _customerIdUseCase.execute(Unit)
            response.fold(
                left = {
                   launch {
                       _snackBarState.emit(it.userMessage)
                   }
                    null
                },
                right = {
                    _customerId = it.customerId
                    _customerId
                }
            )
        }.await()
    }

    private suspend fun _getEphemeralKey(): String?{
        return viewModelScope.async{
            val response = _ephemeralKeyUseCase.execute(EphemeralKeyReq(customerId = _customerId!!))
            response.fold(
                left = {
                    launch {
                        _snackBarState.emit(it.userMessage)
                    }
                    null
                },
                right = {
                    _ephemeralKey = it.key
                    _ephemeralKey
                }
            )
        }.await()
    }

    private suspend fun _getClientSecret(amount: Double): String?{
        return viewModelScope.async {
            val response = _createPaymentIntentUseCase.execute(
                PaymentIntentReq(
                    customerId = _customerId!!,
                    amount = (amount * 100).toInt()),
            )
            response.fold(
                left = {
                    launch {
                        _snackBarState.emit(it.userMessage)
                    }
                    null
                },
                right = {
                    _clientSecret = it.client_secret
                    _clientSecret
                }
            )
        }.await()
    }

    private fun _makePayment(amount: Double, paymentMethod: PaymentMethod){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }
            _getCustomerId()
            if(_customerId != null) _getEphemeralKey()
            if(_customerId != null && _ephemeralKey != null) _getClientSecret(amount)

            _state.update {
                it.copy(
                    requestState = RequestState.IDLE,
                    clientSecret = _clientSecret,
                    customerId = _customerId,
                    ephemeralKey = _ephemeralKey,
                    selectedPaymentMethod = paymentMethod
                )
            }

        }
    }

    private fun _pershesCompleted(totalPrice: Float, paymentMethod: PaymentMethod,
                                  latitude: Double,
                                  longitude: Double){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }

            val response = _perchesCompletedUseCase.execute(
                PerchesCompletedReq(
                    totalPrice = totalPrice,
                    paymentMethod = paymentMethod,
                    status = OrderStatus.PENDING,
                    address = PerchesCompletedReq.Address(
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            )

            response.fold(
                { left ->
                    launch {
                        _snackBarState.emit(left.userMessage)
                    }
                },
                { right ->
                    _state.update {
                        it.copy(
                            requestState = RequestState.IDLE,
                            purchaseCompleted = true
                        )
                    }
                }
            )

        }
    }

}