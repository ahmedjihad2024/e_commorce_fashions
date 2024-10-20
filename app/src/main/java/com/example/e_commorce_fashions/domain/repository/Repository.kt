package com.example.e_commorce_fashions.domain.repository

//
import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.data.requests.CartReq
import com.example.e_commorce_fashions.data.requests.EphemeralKeyReq
import com.example.e_commorce_fashions.data.requests.FavoritesReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceWithOnDataChangeReq
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.data.requests.PaymentIntentReq
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
import com.example.e_commorce_fashions.domain.models.CartItem
import com.example.e_commorce_fashions.domain.models.Categories
import com.example.e_commorce_fashions.domain.models.ClientSecret
import com.example.e_commorce_fashions.domain.models.CustomerId
import com.example.e_commorce_fashions.domain.models.EphemeralKey
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.MyOrders
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.models.UserDetails
import com.example.e_commorce_fashions.domain.models.UserLoginResult
import com.example.e_commorce_fashions.domain.models.UserRegisterResult
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job


interface Repository {
   suspend fun registerUser(user: UserRegisterReq) : Either<Failure<Nothing>, UserRegisterResult>
   suspend fun loginUser(user: UserLoginReq) : Either<Failure<Nothing>, UserLoginResult>
   suspend fun loginWithCredential(authToken: UserLoginAuthTokenReq): Either<Failure<Nothing>, UserLoginResult>
   suspend fun updateUserDetails(user: UserDetailsReq): Either<Failure<Nothing>, Unit>
   suspend fun fetchNewArrivalProducts(req: ProductsReq): Either<Failure<Nothing>, Products>
   suspend fun fetchPopularProducts(req: ProductsReq): Either<Failure<Nothing>, Products>
   suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Either<Failure<Nothing>, Job>
   suspend fun addFavorite(itemReference: ItemReferenceReq) : Either<Failure<Nothing>, Unit>
   suspend fun removeFavorite(itemReference: ItemReferenceReq) : Either<Failure<Nothing>, Unit>
   suspend fun addToCart(cartItem: CartItemReq): Either<Failure<Nothing>, Unit>
   suspend fun listenToCartItem(cartItem: ItemReferenceWithOnDataChangeReq): Either<Failure<Nothing>, ValueEventListener>
   suspend fun removeFromCart(cartItem: ItemReferenceReq): Either<Failure<Nothing>, Unit>
   suspend fun listenToCart(cart: CartReq): Either<Failure<Nothing>, Unit>
   suspend fun getCustomerId(): Either<Failure<Nothing>, CustomerId>
   suspend fun getEphemeralKey(
      ephemeralKeyReq: EphemeralKeyReq
   ): Either<Failure<Nothing>, EphemeralKey>
   suspend fun createPaymentIntent(
      paymentIntentReq: PaymentIntentReq
   ): Either<Failure<Nothing>, ClientSecret>

   suspend fun perchesCompleted(req: PerchesCompletedReq) : Either<Failure<Nothing>, Unit>
   suspend fun getCategories(): Either<Failure<Nothing>, Categories>
   suspend fun search(searchReq: SearchReq): Either<Failure<Nothing>, Products>
   suspend fun getUserDetails(): Either<Failure<Nothing>, UserDetails>
   suspend fun updateProfilePicture(uri: UriReq): Either<Failure<Nothing>, Unit>
   suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq): Either<Failure<Nothing>, Unit>
   suspend fun getOrders(
      req: OrderReq
   ): Either<Failure<Nothing>, MyOrders>
   suspend fun getFavorites(req: FavoritesReq): Either<Failure<Nothing>, Products>
}