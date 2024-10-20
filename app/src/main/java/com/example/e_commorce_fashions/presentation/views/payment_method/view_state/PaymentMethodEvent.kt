package com.example.e_commorce_fashions.presentation.views.payment_method.view_state

import com.example.e_commorce_fashions.app.utils.PaymentMethod

sealed interface PaymentMethodEvent{
    data class MakePayment(val amount: Double, val paymentMethod: PaymentMethod) : PaymentMethodEvent
    data class PurchaseCompleted(
        val totalPrice: Float,
        val paymentMethod: PaymentMethod,
        val latitude: Double,
        val longitude: Double
    ): PaymentMethodEvent
}