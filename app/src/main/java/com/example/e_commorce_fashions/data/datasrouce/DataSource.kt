package com.example.e_commorce_fashions.data.datasrouce

import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.data.InternetConnectivityChecker
import com.example.e_commorce_fashions.data.datasrouce.local.LocalServices
import com.example.e_commorce_fashions.data.datasrouce.remote.ApiServices
import com.example.e_commorce_fashions.data.datasrouce.remote.PaymentApiServices
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.data.requests.CartReq
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
import com.example.e_commorce_fashions.data.responses.ClientSecretResponse
import com.example.e_commorce_fashions.data.responses.CustomerIdResponse
import com.example.e_commorce_fashions.data.responses.EphemeralKeyResponse
import com.example.e_commorce_fashions.data.responses.UserLoginResultResp
import com.example.e_commorce_fashions.data.responses.UserRegisterResultResp
import com.example.e_commorce_fashions.domain.models.CartItem
import com.example.e_commorce_fashions.domain.models.Categories
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.MyOrders
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.models.UserDetails
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


//suspend fun data(){
//    if(internetChecker.hasConnection()){
//        try{
//            // get the data from internet
//        }catch (e: Throwable){
//            // if we want to send the error
//            // or get the data from local
//            throw e
//        }
//    }else{
//        // get the data from local
//    }
//}

interface DataSourceInterface {
    suspend fun registerUser(user: UserRegisterReq) : UserRegisterResultResp
    suspend fun loginUser(user: UserLoginReq): UserLoginResultResp
    suspend fun loginWithCredential(credential: UserLoginAuthTokenReq): UserLoginResultResp
    suspend fun updateUserDetails(user: UserDetailsReq)
    suspend fun fetchNewArrivalProducts(req: ProductsReq): Products
    suspend fun fetchPopularProducts(req: ProductsReq): Products
    suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Job
    suspend fun addFavorite(itemReference: ItemReferenceReq)
    suspend fun removeFavorite(itemReference: ItemReferenceReq)
    suspend fun addToCart(cartItem: CartItemReq)
    suspend fun listenToCartItem(cartItem: ItemReferenceWithOnDataChangeReq): ValueEventListener
    suspend fun removeFromCart(cartItem: ItemReferenceReq)
    suspend fun listenToCart(cart: CartReq)
    suspend fun getCustomerId(): CustomerIdResponse
    suspend fun getEphemeralKey(
        customerId: String,
        stripVersion: String
    ): EphemeralKeyResponse
    suspend fun createPaymentIntent(
        customerId: String,
        amount: Int,
        currency: String,
        enabled: Boolean,
    ): ClientSecretResponse
    suspend fun perchesCompleted(req: PerchesCompletedReq)
    suspend fun getCategories(): Categories
    suspend fun search(searchReq: SearchReq): Products
    suspend fun getUserDetails(): UserDetails
    suspend fun updateProfilePicture(uri: UriReq)
    suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq)
    suspend fun getOrders(req: OrderReq): MyOrders
    suspend fun getFavorites(req: FavoritesReq): Products
}

class DataSource(
    private val apiService: ApiServices,
    private val localServices: LocalServices,
    private val internetChecker: InternetConnectivityChecker,
    private val paymentApiServices: PaymentApiServices
) : DataSourceInterface {
    override suspend fun registerUser(user: UserRegisterReq) : UserRegisterResultResp{
        val response = apiService.registerUser(user)
        if(response.uid != null) Di.preferences.value.setUserToken(response.uid)
        return response
    }

    override suspend fun loginUser(user: UserLoginReq): UserLoginResultResp {
        val response = apiService.loginUser(user)
        if(response.uid != null) Di.preferences.value.setUserToken(response.uid)
        return response

    }

    override suspend fun loginWithCredential(credential: UserLoginAuthTokenReq): UserLoginResultResp {
        val response = apiService.loginWithCredential(credential)
        if(response.uid != null) Di.preferences.value.setUserToken(response.uid)
        return response
    }

    override suspend fun updateUserDetails(user: UserDetailsReq) {
        apiService.updateUserDetails(user)
    }

    override suspend fun fetchNewArrivalProducts(req: ProductsReq): Products {
        return apiService.fetchNewArrivalProducts(req)
    }

    override suspend fun fetchPopularProducts(req: ProductsReq): Products {
        return apiService.fetchPopularProducts(req)
    }

    override suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Job {
        return apiService.favoritesListener(callback)
    }

    override suspend fun addFavorite(itemReference: ItemReferenceReq) {
        apiService.addFavorite(itemReference)
    }

    override suspend fun removeFavorite(itemReference: ItemReferenceReq) {
        apiService.removeFavorite(itemReference)
    }

    override suspend fun addToCart(cartItem: CartItemReq) {
        return apiService.addToCart(cartItem)
    }

    override suspend fun listenToCartItem(
        cartItem: ItemReferenceWithOnDataChangeReq,

    ): ValueEventListener {
        return apiService.listenToCartItem(cartItem)
    }

    override suspend fun removeFromCart(cartItem: ItemReferenceReq) {
        return apiService.removeFromCart(cartItem)
    }

    override suspend fun listenToCart(cart: CartReq) {
        return apiService.listenToCart(cart)
    }

    override suspend fun getCustomerId(): CustomerIdResponse {
        return paymentApiServices.getCustomerId()
    }

    override suspend fun getEphemeralKey(
        customerId: String,
        stripVersion: String
    ): EphemeralKeyResponse {
       return paymentApiServices.getEphemeralKey(customerId, stripVersion)
    }

    override suspend fun createPaymentIntent(
        customerId: String,
        amount: Int,
        currency: String,
        enabled: Boolean
    ): ClientSecretResponse {
       return paymentApiServices.createPaymentIntent(customerId, amount, currency, enabled)
    }

    override suspend fun perchesCompleted(req: PerchesCompletedReq) {
        return apiService.perchesCompleted(req)
    }

    override suspend fun getCategories(): Categories {
        return apiService.getCategories()

    }

    override suspend fun search(searchReq: SearchReq): Products {
        return apiService.search(searchReq)
    }

    override suspend fun getUserDetails(): UserDetails {
        return apiService.getUserDetails()
    }

    override suspend fun updateProfilePicture(uri: UriReq) {
        return apiService.updateProfilePicture(uri)
    }

    override suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq) {
        return apiService.updateNameAndEmail(userDetailsReq)
    }

    override suspend fun getOrders(req: OrderReq): MyOrders {
        return apiService.getOrders(req)
    }

    override suspend fun getFavorites(req: FavoritesReq): Products {
        return apiService.getFavorites(req)
    }
}