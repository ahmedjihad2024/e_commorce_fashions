package com.example.e_commorce_fashions.presentation.views.payment_method.view_state

import com.example.e_commorce_fashions.app.utils.PaymentMethod
import com.example.e_commorce_fashions.app.utils.RequestState

data class PaymentMethodUiState(
    val requestState: RequestState = RequestState.IDLE,
    val selectedPaymentMethod: PaymentMethod? = null,
    val clientSecret: String? = null,
    val customerId: String? = null,
    val ephemeralKey: String? = null,
    val purchaseCompleted: Boolean = false
)