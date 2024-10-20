package com.example.e_commorce_fashions.data.repository

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.DataSource
import com.example.e_commorce_fashions.data.InternetConnectivityChecker
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.datasrouce.remote.errorHandlerBlock
import com.example.e_commorce_fashions.data.mapper.toDomain
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
import com.example.e_commorce_fashions.domain.repository.Repository
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job

class RepositoryImpl(
    private val dataSource: DataSource,
    private val internetChecker: InternetConnectivityChecker
) : Repository {

    override suspend fun registerUser(user: UserRegisterReq): Either<Failure<Nothing>, UserRegisterResult> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.registerUser(user)
            Either.Right(response.toDomain)
        }
    }

    override suspend fun loginUser(user: UserLoginReq): Either<Failure<Nothing>, UserLoginResult> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.loginUser(user)
            Either.Right(response.toDomain)
        }
    }

    override suspend fun loginWithCredential(authToken: UserLoginAuthTokenReq): Either<Failure<Nothing>, UserLoginResult> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.loginWithCredential(authToken)
            Either.Right(response.toDomain)
        }
    }

    override suspend fun updateUserDetails(user: UserDetailsReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.updateUserDetails(user)
            Either.Right(Unit)
        }
    }

    override suspend fun fetchNewArrivalProducts(req: ProductsReq): Either<Failure<Nothing>, Products> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.fetchNewArrivalProducts(req)
            Either.Right(response)
        }
    }

    override suspend fun fetchPopularProducts(req: ProductsReq): Either<Failure<Nothing>, Products> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.fetchPopularProducts(req)
            Either.Right(response)
        }
    }

    override suspend fun favoritesListener(callback: (favorites: Favorites) -> Unit): Either<Failure<Nothing>, Job> {
        return errorHandlerBlock(internetChecker){
           val response = dataSource.favoritesListener(callback)
            Either.Right(response)
        }
    }

    override suspend fun addFavorite(itemReference: ItemReferenceReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.addFavorite(itemReference)
            Either.Right(Unit)
        }
    }

    override suspend fun removeFavorite(itemReference: ItemReferenceReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.removeFavorite(itemReference)
            Either.Right(Unit)
        }
    }

    override suspend fun addToCart(cartItem: CartItemReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.addToCart(cartItem)
            Either.Right(Unit)
        }
    }

    override suspend fun listenToCartItem(
        cartItem: ItemReferenceWithOnDataChangeReq,
    ): Either<Failure<Nothing>, ValueEventListener> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.listenToCartItem(cartItem)
            Either.Right(response)
        }
    }

    override suspend fun removeFromCart(cartItem: ItemReferenceReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.removeFromCart(cartItem)
            Either.Right(Unit)
        }
    }

    override suspend fun listenToCart(cart: CartReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.listenToCart(cart)
            Either.Right(Unit)
        }
    }

    override suspend fun getCustomerId(): Either<Failure<Nothing>, CustomerId> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.getCustomerId()
            Either.Right(response.toDomain)
        }
    }

    override suspend fun getEphemeralKey(ephemeralKeyReq: EphemeralKeyReq): Either<Failure<Nothing>, EphemeralKey> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.getEphemeralKey(ephemeralKeyReq.customerId, ephemeralKeyReq.stripVersion)
            Either.Right(response.toDomain)
        }
    }

    override suspend fun createPaymentIntent(
        paymentIntentReq: PaymentIntentReq
    ): Either<Failure<Nothing>, ClientSecret> {
        return errorHandlerBlock(internetChecker){
            val response = dataSource.createPaymentIntent(
                paymentIntentReq.customerId,
                paymentIntentReq.amount,
                paymentIntentReq.currency,
                paymentIntentReq.enabled
            )
            Either.Right(response.toDomain)
        }
    }

    override suspend fun perchesCompleted(req: PerchesCompletedReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.perchesCompleted(req)
            Either.Right(Unit)
        }
    }

    override suspend fun getCategories(): Either<Failure<Nothing>, Categories> {
        return errorHandlerBlock(internetChecker){
            Either.Right(dataSource.getCategories())
        }
    }

    override suspend fun search(searchReq: SearchReq): Either<Failure<Nothing>, Products> {
        return errorHandlerBlock(internetChecker){
            Either.Right(dataSource.search(searchReq))
        }
    }

    override suspend fun getUserDetails(): Either<Failure<Nothing>, UserDetails> {
        return errorHandlerBlock(internetChecker){
            Either.Right(dataSource.getUserDetails())
        }
    }

    override suspend fun updateProfilePicture(uri: UriReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.updateProfilePicture(uri)
            Either.Right(Unit)
        }
    }

    override suspend fun updateNameAndEmail(userDetailsReq: UserDetailsReq): Either<Failure<Nothing>, Unit> {
        return errorHandlerBlock(internetChecker){
            dataSource.updateNameAndEmail(userDetailsReq)
            Either.Right(Unit)
        }
    }

    override suspend fun getOrders(req: OrderReq): Either<Failure<Nothing>, MyOrders> {
        return errorHandlerBlock(internetChecker){
            Either.Right(dataSource.getOrders(req))
        }
    }

    override suspend fun getFavorites(req: FavoritesReq): Either<Failure<Nothing>, Products> {
        return errorHandlerBlock(internetChecker){
            Either.Right(
                dataSource.getFavorites(req)
            )
        }
    }

}


