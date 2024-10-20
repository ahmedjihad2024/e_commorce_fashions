package com.example.e_commorce_fashions.domain.models

import com.example.e_commorce_fashions.app.utils.OrderStatus
import com.example.e_commorce_fashions.app.utils.PaymentMethod
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot


data class UserRegisterResult(
    val uid: String,
)

data class UserLoginResult(val uid: String)


data class ProductDetails(
    val name: String = "",
    val category: DocumentReference? = null, // Allow nullable DocumentReference
    val price: Double = 0.0,
    val description: String = "",
    val imageUrls: List<String>? = null,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val reviews: Int = 0,
    val rating: Double = 0.0,
    val available: Boolean = false,
    val timestamp: Timestamp = Timestamp.now(),
    var productReference: DocumentReference? = null,
    var isFavorite: Boolean = false,
    var isInCart: Boolean = false
)

data class Products(
    val products: List<ProductDetails>,
    val lastDocument: DocumentSnapshot?
)


data class Favorites(
    val favorites: List<DocumentReference>
) {
    fun isInFavorites(productReference: DocumentReference): Boolean =
        favorites.contains(productReference)
}


data class CartItem(
    val productReference: DocumentReference? = null,
    val quantity: Int? = null,
    val size: String? = null,
    val color: String? = null
)

data class CartItemDetails(
    var productDetails: ProductDetails? = null,
    var quantity: Int? = null,
    var size: String? = null,
    var color: String? = null,
)

data class CartItemRemoved(
    val productId: String? = null
)

data class CartItemUpdated(
    val productId: String? = null,
    val quantity: Int? = null,
    val size: String? = null,
    val color: String? = null
)


data class CustomerId(
    val customerId: String
)

data class EphemeralKey(
    val key: String
)

data class ClientSecret(
    val client_secret: String
)

data class CategoryDetails(
    val name: String,
    val reference: DocumentReference
)

data class Categories(
    val categories: List<CategoryDetails>
)

data class UserDetails(
    val name: String? = null,
    val email: String? = null,
    val pictureUrl: String? = null,
){
    constructor(map: Map<String, Any?>) : this (
        name = map["name"] as? String,
        email = map["email"] as? String,
        pictureUrl = map["pictureUrl"] as? String,
    )
}

data class OrderLatLng(
    val latitude: Double,
    val longitude: Double
)

data class OrderDetails(
    val latLng: OrderLatLng,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val totalPrice: Double,
    val key: String
){
    constructor(data: DataSnapshot): this (
        latLng = OrderLatLng(
            latitude = (data.value as Map<String, Any>)["latitude"] as Double,
            longitude = (data.value as Map<String, Any>)["longitude"] as Double
        ),
        paymentMethod = PaymentMethod.valueOf((data.value as Map<String, Any>)["paymentMethod"] as String),
        status = OrderStatus.valueOf((data.value as Map<String, Any>)["status"] as String),
        totalPrice = (data.value as Map<String, Any>)["totalPrice"] as Double,
        key = data.key!!
    )
}

data class MyOrders(
    val orders: List<OrderDetails>,
    val lastKey: String? = null
){
    constructor(data: Iterable<DataSnapshot>): this (
        orders = data.map { OrderDetails(it) },
        lastKey = data.lastOrNull()?.key
    )
}



