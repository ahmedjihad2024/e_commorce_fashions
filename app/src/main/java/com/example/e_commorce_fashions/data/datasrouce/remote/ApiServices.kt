package com.example.e_commorce_fashions.data.datasrouce.remote

import android.net.Uri
import com.example.e_commorce_fashions.app.utils.Constants.CART_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.CATEGORIES_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.FAVORITES_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.NEW_ARRIVAL_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.ORDERS_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.POPULAR_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.PRODUCTS_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.PROFILE_PICTURE_ROOT_PATH
import com.example.e_commorce_fashions.app.utils.Constants.USERS_COLLECTION
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.data.requests.AuthType
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.data.requests.CartReq
import com.example.e_commorce_fashions.data.requests.CreateOrderRequest
import com.example.e_commorce_fashions.data.requests.FavoritesReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceWithOnDataChangeReq
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.data.requests.PerchesCompletedReq
import com.example.e_commorce_fashions.data.requests.ProductsReq
import com.example.e_commorce_fashions.data.requests.SearchReq
import com.example.e_commorce_fashions.data.requests.UriReq
import com.example.e_commorce_fashions.data.requests.UserDetailsReq
import com.example.e_commorce_fashions.data.requests.UserLoginAuthTokenReq
import com.example.e_commorce_fashions.data.requests.UserLoginReq
import com.example.e_commorce_fashions.data.requests.UserRegisterReq
import com.example.e_commorce_fashions.data.responses.AccessTokenResponse
import com.example.e_commorce_fashions.data.responses.ClientSecretResponse
import com.example.e_commorce_fashions.data.responses.CustomerIdResponse
import com.example.e_commorce_fashions.data.responses.EphemeralKeyResponse
import com.example.e_commorce_fashions.data.responses.OrderResponse
import com.example.e_commorce_fashions.data.responses.UserLoginResultResp
import com.example.e_commorce_fashions.data.responses.UserRegisterResultResp
import com.example.e_commorce_fashions.domain.models.CartItem
import com.example.e_commorce_fashions.domain.models.CartItemDetails
import com.example.e_commorce_fashions.domain.models.CartItemRemoved
import com.example.e_commorce_fashions.domain.models.CartItemUpdated
import com.example.e_commorce_fashions.domain.models.Categories
import com.example.e_commorce_fashions.domain.models.CategoryDetails
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.MyOrders
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.models.UserDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiServicesInterface {
    suspend fun registerUser(user: UserRegisterReq): UserRegisterResultResp
    suspend fun loginUser(user: UserLoginReq): UserLoginResultResp
    suspend fun loginWithCredential(authToken: UserLoginAuthTokenReq): UserLoginResultResp
    suspend fun updateUserDetails(user: UserDetailsReq)
    suspend fun fetchNewArrivalProducts(req: ProductsReq): Products
    suspend fun fetchPopularProducts(req: ProductsReq): Products
    suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Job
    suspend fun addFavorite(itemReference: ItemReferenceReq)
    suspend fun removeFavorite(itemReference: ItemReferenceReq)
    suspend fun addToCart(cartItem: CartItemReq)
    suspend fun removeFromCart(cartItem: ItemReferenceReq)
    suspend fun listenToCartItem(cartItem: ItemReferenceWithOnDataChangeReq): ValueEventListener
    suspend fun getProductById(id: String): ProductDetails
    suspend fun listenToCart(cart: CartReq)
    suspend fun perchesCompleted(req: PerchesCompletedReq)
    suspend fun getCategories(): Categories
    suspend fun search(searchReq: SearchReq): Products
    suspend fun getUserDetails(): UserDetails
    suspend fun updateProfilePicture(uri: UriReq)
    suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq)
    suspend fun getOrders(
        req: OrderReq
    ): MyOrders
    suspend fun getFavorites(req: FavoritesReq): Products
}

class ApiServices : ApiServicesInterface {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val rt = Firebase.database.reference
    private val storage = Firebase.storage.reference
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun registerUser(user: UserRegisterReq): UserRegisterResultResp {
        val result = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        return UserRegisterResultResp(result.user?.uid)
    }

    override suspend fun loginUser(user: UserLoginReq): UserLoginResultResp {
        val result = auth.signInWithEmailAndPassword(user.email, user.password).await()
        return UserLoginResultResp(result.user?.uid)
    }

    override suspend fun loginWithCredential(authToken: UserLoginAuthTokenReq): UserLoginResultResp {
        val authCredential = when (authToken.authType) {
            AuthType.GOOGLE -> GoogleAuthProvider.getCredential(authToken.token, null)
            AuthType.FACEBOOK -> FacebookAuthProvider.getCredential(authToken.token)
        }
        val result = auth.signInWithCredential(authCredential).await()
        return UserLoginResultResp(result.user?.uid)
    }

    override suspend fun updateUserDetails(user: UserDetailsReq) {
        val builder = UserProfileChangeRequest.Builder()

        if (user.name != null && auth.currentUser!!.displayName == null) builder.setDisplayName(user.name)
        if (user.pictureUrl != null && auth.currentUser!!.photoUrl == null) builder.setPhotoUri(
            Uri.parse(
                user.pictureUrl
            )
        )

        auth.currentUser!!.updateProfile(builder.build())

//        val docData: MutableMap<String, Any> = HashMap()
//        user.name?.let { docData["name"] = it }
//        user.email?.let { docData["email"] = it }
//        user.pictureUrl?.let { docData["pictureUrl"] = it }
//
//        // Use FieldValue.arrayUnion to add items to lists
//        user.favorites?.let { docData["favorites"] = FieldValue.arrayUnion(*it.toTypedArray()) }
//
//        user.cart?.let {
//            docData["cart"] = it
//        }
//
//        user.addresses?.let { docData["addresses"] = FieldValue.arrayUnion(*it.toTypedArray()) }
//        user.country?.let { docData["country"] = it }
//        user.city?.let { docData["city"] = it }
//        user.zipCode?.let { docData["zipCode"] = it }
//        user.streetAddress?.let { docData["streetAddress"] = it }
//
//        if (docData.isEmpty()) return
//
//        val docRef =
//            db.collection(USERS_COLLECTION).document(Di.preferences.value.getUserToken().first()!!)
//        if (docRef.get().await().exists()) {
//            docRef.update(docData)
//                .await()
//        } else {
//            docData["name"] = user.name ?: ""
//            docData["email"] = user.email ?: ""
//            docData["pictureUrl"] = user.pictureUrl ?: ""
//            docData["favorites"] = user.favorites ?: emptyList<String>()
//            docData["cart"] = user.cart ?: emptyMap<String, Any>()
//            docData["addresses"] = user.addresses ?: emptyList<String>()
//            docData["country"] = user.country ?: ""
//            docData["city"] = user.city ?: ""
//            docData["zipCode"] = user.zipCode ?: ""
//            docData["streetAddress"] = user.streetAddress ?: ""
//            docRef.set(docData)
//                .await()
//        }
    }

    override suspend fun fetchNewArrivalProducts(req: ProductsReq): Products {

        val productsGroup = mutableListOf<ProductDetails>()
        val query = db.collection(NEW_ARRIVAL_COLLECTION)
            .orderBy("itemReference")
            .startAfter(req.lastDocument?.get("itemReference"))
            .limit(req.limit)


        val snapshot = query.get().await()
        if (snapshot.isEmpty) return Products(emptyList(), null)

        snapshot.documents.forEach {
            val result = it.getDocumentReference("itemReference")!!.get().await()
            val product = result.toObject(ProductDetails::class.java)!!
            product.productReference = result.reference
            productsGroup.add(product)
        }

        val lastDocument = snapshot.documents.lastOrNull()
        return Products(productsGroup, lastDocument)
    }

    override suspend fun fetchPopularProducts(req: ProductsReq): Products {
        val productsGroup = mutableListOf<ProductDetails>()
        val query = db.collection(POPULAR_COLLECTION)
            .orderBy("itemReference")
            .startAfter(req.lastDocument?.get("itemReference"))
            .limit(req.limit)


        val snapshot = query.get().await()
        if (snapshot.isEmpty) return Products(emptyList(), null)

        snapshot.documents.forEach {
            val result = it.getDocumentReference("itemReference")!!.get().await()
            val product = result.toObject(ProductDetails::class.java)!!
            product.productReference = result.reference
            productsGroup.add(product)
        }

        val lastDocument = snapshot.documents.lastOrNull()
        return Products(productsGroup, lastDocument)
    }

    override suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Job {
        val docRef =
            db.collection(USERS_COLLECTION).document(Di.preferences.value.getUserToken().first()!!)
                .collection(FAVORITES_COLLECTION)

        return CoroutineScope(Dispatchers.IO).launch {
            val snapshots = docRef.snapshots()
            currentCoroutineContext().ensureActive()
            snapshots.collect { it ->
                try {
                    currentCoroutineContext().ensureActive()
                    val favorites = Favorites(
                        it.documents.map {
                            it.getDocumentReference("itemReference")!!
                        }
                    )
                    callback(favorites)
                } catch (_: Exception) {
                }
            }
        }
    }

    override suspend fun addFavorite(itemReference: ItemReferenceReq) {
        val request =
            db.collection(USERS_COLLECTION)
                .document(Di.preferences.value.getUserToken().first()!!)
                .collection(FAVORITES_COLLECTION)
                .document(itemReference.itemReference.id)
                .set(
                    mapOf(
                        "itemReference" to itemReference.itemReference
                    )
                )
        request.await()
    }

    override suspend fun removeFavorite(itemReference: ItemReferenceReq) {
        val request =
            db.collection(USERS_COLLECTION)
                .document(Di.preferences.value.getUserToken().first()!!)
                .collection(FAVORITES_COLLECTION)
                .document(itemReference.itemReference.id)
        request.delete().await()
    }

    override suspend fun addToCart(cartItem: CartItemReq) {
        val data: MutableMap<String, Any?> = mutableMapOf()
        if (cartItem.productReference != null) data["productId"] = cartItem.productReference.id
        if (cartItem.quantity != null) data["quantity"] = cartItem.quantity
        if (cartItem.size != null) data["size"] = cartItem.size
        if (cartItem.color != null) data["color"] = cartItem.color

        rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(CART_COLLECTION)
            .child(cartItem.cartItemId)
            .updateChildren(data)
            .await()
    }

    override suspend fun removeFromCart(cartItem: ItemReferenceReq) {
        rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(CART_COLLECTION)
            .child(cartItem.itemReference.id)
            .removeValue()
            .await()
    }

    override suspend fun listenToCartItem(cartItem: ItemReferenceWithOnDataChangeReq): ValueEventListener {
        val dataRef = rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(CART_COLLECTION)
            .child(cartItem.itemReferenceReq.itemReference.id)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItem.onDataChange(snapshot.getValue(CartItem::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                cartItem.onCanceled?.invoke()
            }
        }

        dataRef.addValueEventListener(valueEventListener)

        cartItem.valueEventListener = valueEventListener
        cartItem.refrence = dataRef

        return valueEventListener
    }

    override suspend fun getProductById(id: String): ProductDetails {


        val result = db.collection(PRODUCTS_COLLECTION).document(id).get().await()

        return ProductDetails(
            productReference = result.reference,
            name = result.getString("name") ?: "",
            available = result.getBoolean("available") ?: false,
            category = result.getDocumentReference("category"),
            price = result.getDouble("price") ?: 0.0,
            description = result.getString("description") ?: "",
            imageUrls = result.data?.get("imageUrls")?.let { it as List<String> },
            colors = result.data?.get("colors") as List<String>,
            sizes = result.data?.get("sizes") as List<String>,
            timestamp = result.getTimestamp("timestamp")!!,
            reviews = result.getLong("reviews")?.toInt() ?: 0,
            rating = result.getDouble("rating") ?: 0.0,
        )

    }


    override suspend fun listenToCart(cart: CartReq) {

        val dataRef = rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(CART_COLLECTION)

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                scope.launch {
                    val cartItemDetails = CartItemDetails()

                    cartItemDetails.size = snapshot.child("size").value as String
                    cartItemDetails.color = snapshot.child("color").value as String
                    cartItemDetails.quantity =
                        snapshot.child("quantity").value?.let { it as Long }?.toInt()
                    cartItemDetails.productDetails = try {
                        println(snapshot.key!!)
                        getProductById(snapshot.key!!)
                    } catch (e: Throwable) {
                        null
                    }

                    cart.onCartItemAdded(cartItemDetails)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cart.onCartItemUpdated(
                    CartItemUpdated(
                        size = snapshot.child("size").value as String,
                        color = snapshot.child("color").value as String,
                        quantity = snapshot.child("quantity").value?.let { it as Long }?.toInt(),
                        productId = snapshot.key
                    )
                )
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                cart.onCartItemRemoved(
                    CartItemRemoved(
                        productId = snapshot.key
                    )
                )
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        cart.childEventListener = childEventListener
        cart.refrence = dataRef

        dataRef.addChildEventListener(childEventListener)

    }

    override suspend fun perchesCompleted(req: PerchesCompletedReq) {
        rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(ORDERS_COLLECTION).child(rt.push().key!!).setValue(
                mapOf(
                    "totalPrice" to req.totalPrice,
                    "status" to req.status,
                    "paymentMethod" to req.paymentMethod.name,
                    "latitude" to req.address.latitude,
                    "longitude" to req.address.longitude
                )
            ).await()

        rt.child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(CART_COLLECTION).removeValue().await()
    }

    override suspend fun getCategories(): Categories {

        val categories = mutableListOf<CategoryDetails>()

        val snapshot: QuerySnapshot = db.collection(CATEGORIES_COLLECTION).get().await()
        for (document in snapshot.documents) {
            categories.add(
                CategoryDetails(
                    name = document.get("name") as String,
                    reference = document.reference
                )
            )
        }

        return Categories(categories)

    }

    override suspend fun search(
        searchReq: SearchReq
    ): Products {

        val productsGroup = mutableListOf<ProductDetails>()

        var query = db.collection(PRODUCTS_COLLECTION)
            .orderBy("category")
            .startAfter(searchReq.lastDocument?.get("category"))
            .limit(searchReq.limit)

        if (searchReq.categoryReference != null) {
            query = query.whereEqualTo("category", searchReq.categoryReference)
        }

        query = query
            .whereGreaterThanOrEqualTo(
                "name",
                searchReq.about
            ) // Assuming searchReq.name is the start of the name
//            .whereLessThanOrEqualTo("name", searchReq.about) //searchReq.name + "\uf8ff"
//            .whereArrayContainsAny("name", searchReq.about.split(""))

        if (
            searchReq.minPrice != null
        ) query = query.whereGreaterThanOrEqualTo("price", searchReq.minPrice)

        if (
            searchReq.maxPrice != null
        ) query = query.whereLessThanOrEqualTo("price", searchReq.maxPrice)


        val snapshot = query.get().await()
        if (snapshot.isEmpty) return Products(emptyList(), null)

        snapshot.documents.forEach {
            val product = it.toObject(ProductDetails::class.java)!!
            product.productReference = it.reference
            productsGroup.add(product)
        }

        val lastDocument = snapshot.documents.lastOrNull()
        return Products(productsGroup, lastDocument)
    }


    override suspend fun getUserDetails(): UserDetails {

//        val docRef =
//            db.collection(USERS_COLLECTION).document(Di.preferences.value.getUserToken().first()!!)
//        val reference: DocumentSnapshot = docRef.get().await()
//
//        return UserDetails(reference.data!!)

        auth.currentUser!!.reload().await()

        return UserDetails(
            name = auth.currentUser!!.displayName,
            email = auth.currentUser!!.email,
            pictureUrl = auth.currentUser!!.photoUrl?.toString()
        )
    }

    override suspend fun updateProfilePicture(uri: UriReq) {
        val reference = storage.child(
            "${PROFILE_PICTURE_ROOT_PATH}/${
                Di.preferences.value.getUserToken().first()!!
            }/profile-picture.${
                uri.uri.toString().split(".").last()
            }"
        )
        reference.putFile(uri.uri).await()
        val downloadUrl = reference.downloadUrl.await()

        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).build()
        )
    }

    override suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq) {
        if (userDetailsReq.name != null) {
            auth.currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(userDetailsReq.name).build()
            )
        }

        if (userDetailsReq.email != null) {
            auth.currentUser!!.verifyBeforeUpdateEmail(userDetailsReq.email).await()
        }
    }


    override suspend fun getOrders(
        req: OrderReq
    ): MyOrders {
        var query = rt
            .child(USERS_COLLECTION)
            .child(Di.preferences.value.getUserToken().first()!!)
            .child(ORDERS_COLLECTION)
            .limitToFirst(req.limit)
            .orderByKey()

        if (req.lastKey != null) query = query.startAfter(req.lastKey)

        val dataSnapshot = query.get().await()

        return MyOrders(dataSnapshot.children)
    }

    override suspend fun getFavorites(req: FavoritesReq): Products {
        val productsGroup = mutableListOf<ProductDetails>()

        var docRef =
            db.collection(USERS_COLLECTION).document(Di.preferences.value.getUserToken().first()!!)
                .collection(FAVORITES_COLLECTION)
                .limit(req.limit)

        if (
            req.lastItem != null
        ) docRef = docRef.startAfter(req.lastItem)

        val response = docRef.get().await()

        for (document in response.documents) {
            productsGroup.add(getProductById(document.id).copy(
                isFavorite = true
            ))
        }

        val lastDocument = response.documents.lastOrNull()
        return Products(productsGroup, lastDocument)
    }

}


interface PaymentApiServices {
    @POST("customers")
    suspend fun getCustomerId(): CustomerIdResponse

    @POST("ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customerId: String,
        @Header("Stripe-Version") stripVersion: String = "2024-06-20"
    ): EphemeralKeyResponse

    @POST("payment_intents")
    suspend fun createPaymentIntent(
        @Query("customer") customerId: String,
        @Query("amount") amount: Int,
        @Query("currency") currency: String = "usd",
        @Query("automatic_payment_methods[enabled]") enabled: Boolean = true,
    ): ClientSecretResponse
}

interface PaypalPaymentApiServices {
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Field("intent") intent: String,
        @Body orderRequest: CreateOrderRequest
    ): OrderResponse
}




