package com.example.e_commorce_fashions.data.requests

import android.net.Uri
import com.example.e_commorce_fashions.app.utils.OrderStatus
import com.example.e_commorce_fashions.app.utils.PaymentMethod
import com.example.e_commorce_fashions.domain.models.CartItem
import com.example.e_commorce_fashions.domain.models.CartItemDetails
import com.example.e_commorce_fashions.domain.models.CartItemRemoved
import com.example.e_commorce_fashions.domain.models.CartItemUpdated
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

enum class AuthType{
    GOOGLE,
    FACEBOOK
}
data class UserRegisterReq(
    val name: String,
    val email: String,
    val password: String,
)

data class UserLoginReq(
    val email: String,
    val password: String,
)

data class UserLoginAuthTokenReq(
    val token: String,
    val authType: AuthType
)

data class UserDetailsReq(
    val name: String? = null,
    val email: String?= null,
    val pictureUrl: String?= null,
    val favorites: List<String>?= null,
    val cart: Map<String, Map<String, Any?>>?= null,
//    val orders: List<String>?,
    val addresses: List<String>?= null,
    val country: String?= null,
    val city: String?= null,
    val zipCode: String?= null,
    val streetAddress: String?= null,
)


data class ProductsReq(
    val limit: Long,
    val lastDocument: DocumentSnapshot?
)

data class ItemReferenceReq(
    val itemReference: DocumentReference,
)

data class ItemReferenceWithOnDataChangeReq(
    val itemReferenceReq: ItemReferenceReq,
    val onDataChange: (cart: CartItem?) -> Unit,
    val onCanceled: (() -> Unit)? = null
){
    var valueEventListener: ValueEventListener? = null
    var refrence: DatabaseReference? = null

    fun cancelListener(){
        refrence?.removeEventListener(valueEventListener!!)
    }
}

data class CartItemReq(
    val cartItemId: String,
    val productReference: DocumentReference? = null,
    val quantity: Int? = null,
    val size: String? = null,
    val color: String? = null,
)

data class CartReq(
    val onCartItemAdded: (cart: CartItemDetails) -> Unit,
    val onCartItemRemoved: (cart: CartItemRemoved) -> Unit,
    val onCartItemUpdated: (cart: CartItemUpdated) -> Unit,
){
    var childEventListener: ChildEventListener? = null
    var refrence: DatabaseReference? = null

    fun cancelListener(){
        if(childEventListener != null) refrence?.removeEventListener(childEventListener!!)
    }
}

data class EphemeralKeyReq(
    val customerId: String,
    val stripVersion: String = "2024-06-20"
)

data class PaymentIntentReq(
    val customerId: String,
    val amount: Int,
    val currency: String = "usd",
    val enabled: Boolean = true,
    )



data class PurchaseUnitAmount(
    val currency_code: String,
    val value: String
)

data class PurchaseUnit(
    val amount: PurchaseUnitAmount
)

data class CreateOrderRequest(
    val intent: String,
    val purchase_units: List<PurchaseUnit>
)

data class PerchesCompletedReq(
    val totalPrice: Float,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val address: Address
){
    data class Address(
        val latitude: Double,
        val longitude: Double,
    )
}

data class SearchReq(
    val limit: Long,
    val about: String,
    val categoryReference: DocumentReference? = null,
    val lastDocument: DocumentSnapshot?,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
)

data class UriReq(
    val uri: Uri,
)

data class OrderReq(
    val lastKey: String?,
    val limit: Int,
)

data class FavoritesReq(
    val lastItem: DocumentSnapshot?,
    val limit: Long,
)