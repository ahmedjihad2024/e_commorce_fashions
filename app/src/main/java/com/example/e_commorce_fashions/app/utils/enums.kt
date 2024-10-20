package com.example.e_commorce_fashions.app.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.core.content.ContextCompat
import com.example.e_commorce_fashions.R

//enum class RequestState {
//    LOADING,
//    ERROR,
//    EMPTY,
//    SUCCESS,
//    NO_INTERNET,
//    LOADING_MORE,
//    IDLE
//}

@Immutable
sealed class RequestState {
    data object SUCCESS : RequestState()
    data class ERROR(val errorMessage: String = "") : RequestState()
    data object LOADING: RequestState()
//    sealed class LOADING : RequestState(){
//        data object INITIAL : LOADING()
//        data object MORE : LOADING()
//        data object REFRESH : LOADING()
//        data object NO_MORE : LOADING()
//    }
    data object NO_INTERNET : RequestState()
    data object EMPTY : RequestState()
    data object IDLE : RequestState()
}

enum class PaymentMethod(@StringRes val string: Int, @DrawableRes val drawable: Int, val value: String){
    CREDIT_CARD(R.string.credit_card, R.drawable.ic_credit_card, "card"),
    PAYPAL(R.string.paypal, R.drawable.ic_paypal, "paypal"),
    VISA(R.string.visa, R.drawable.ic_visa, "card"),
    GOOGLE_PAY(R.string.google_pay, R.drawable.ic_google_pay, "google_pay");

    companion object{
        fun from(value:String): PaymentMethod?{
            return entries.find { it.name == value }
        }
    }
}

enum class OrderStatus{
    PENDING,
    SHIPPED,
    DELIVERED;

    companion object{
        fun from(value:String): OrderStatus?{
            return entries.find { it.name == value }
        }
    }
}