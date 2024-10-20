package com.example.e_commorce_fashions.presentation.views.cart.view_state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.data.requests.CartReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.domain.models.CartItem
import com.example.e_commorce_fashions.domain.models.CartItemDetails
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.AddToCartUseCase
import com.example.e_commorce_fashions.domain.use_case.FavoritesListenerUseCase
import com.example.e_commorce_fashions.domain.use_case.ListenToCartUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFromCartUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewStateFactory(private val repository: Repository = Di.repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewState::class.java)) {
            return CartViewState(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CartViewState(repository: Repository) : ViewModel() {

    private var _isListenerStart = false
    private val _state = MutableStateFlow(UiCartState())
    val state: StateFlow<UiCartState> = _state.onStart {
        if (!_isListenerStart) {
            _fetchCartItemsListener()
            _fetchFavorites()
            _isListenerStart = true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiCartState())


    private val _cartItemsListener = ListenToCartUseCase(repository)
    private val _removeFromCartUseCase = RemoveFromCartUseCase(repository)
    private val _addToCartUseCase = AddToCartUseCase(repository)
    private val _favoritesUseCase = FavoritesListenerUseCase(repository)

    private lateinit var _cartReq: CartReq
    private var _favoritesJob: Job? = null
    private var _favorites = Favorites(emptyList())

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.RemoveItem -> {
                _removeFromCart(event.item)
            }

            is CartEvent.IncreaseQuantity -> {
                _increaseQuantity(event.item)
            }

            is CartEvent.DecreaseQuantity -> {
                _decreaseQuantity(event.item)
            }

            is CartEvent.ReloadPage -> {
                _favoritesJob?.cancel()
                _fetchFavorites()
                _fetchCartItemsListener()
            }
        }
    }

    private fun _fetchFavorites() {
        viewModelScope.launch {
            val result = _favoritesUseCase.execute {
                _favorites = it
                _updateProductFavoriets()
            }

            result.fold(
                left = { },
                right = { right ->
                    _favoritesJob = right
                }
            )
        }
    }

    private fun _fetchCartItemsListener() {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }

            _cartReq = CartReq(
                onCartItemAdded = { newItem ->
                    _state.update {
                        it.copy(
                            requestState = RequestState.SUCCESS,
                            cartItems = _state.value.cartItems.apply {
                                add(
                                    newItem.copy(
                                        productDetails = newItem.productDetails!!.handleFavorites()
                                    )
                                )
                                                                     },
                            itemCount = _state.value.itemCount + 1,
                            total = _state.value.total + ((newItem.productDetails?.price
                                ?: 0.0) * newItem.quantity!!.toFloat())
                        )
                    }
                },
                onCartItemRemoved = { removedItem ->
                    var removedItemDetails: CartItemDetails? = null
                    val isRemoved: Boolean =
                        _state.value.cartItems.removeIf { item ->
                            if (item.productDetails?.productReference?.id == removedItem.productId) {
                                removedItemDetails = item
                                true
                            } else false
                        }
                    if (isRemoved) {
                        _state.update {
                            if (_state.value.cartItems.isEmpty()) {
                                it.copy(
                                    requestState = RequestState.EMPTY,
                                    cartItems = mutableSetOf(),
                                    itemCount = 0,
                                    total = 0.0
                                )
                            } else {
                                it.copy(
                                    cartItems = _state.value.cartItems,
                                    itemCount = _state.value.itemCount - 1,
                                    total = _state.value.total - ((removedItemDetails!!.productDetails?.price
                                        ?: 0.0) * removedItemDetails!!.quantity!!.toFloat())
                                )
                            }
                        }
                    }

                },
                onCartItemUpdated = { updatedItem ->
                    _state.value.cartItems.find<CartItemDetails> { it.productDetails?.productReference?.id == updatedItem.productId }
                        ?.apply {

                            var newTotal = _state.value.total
                            if (quantity != updatedItem.quantity) {
                                val diff = updatedItem.quantity!! - quantity!!
                                newTotal =
                                    _state.value.total + (diff * (productDetails?.price ?: 0.0))
                            }

                            quantity = updatedItem.quantity
                            size = updatedItem.size
                            color = updatedItem.color

                            _state.update {
                                it.copy(
                                    cartItems = _state.value.cartItems,
                                    total = newTotal
                                )
                            }

                        }
                }
            )
            val response = _cartItemsListener.execute(_cartReq)
            response.fold(
                left = { left ->
                    _cartReq.cancelListener()
                    _state.update {
                        it.copy(
                            errorMessage = left.userMessage,
                            requestState = RequestState.ERROR()
                        )
                    }
                },
                right = {
                    _state.update {
                        it.copy(
                            requestState = RequestState.EMPTY,
                        )
                    }
                }
            )

        }
    }


    private fun _removeFromCart(
        item: CartItemDetails
    ) {
        viewModelScope.launch {

//            val indexOfItem = _state.value.cartItems.indexOf(item)

//            _state.update {
//                it.copy(
//                    cartItems = _state.value.cartItems.apply { removeAt(indexOfItem) },
//                )
//            }

            val referenceReq = ItemReferenceReq(item.productDetails?.productReference!!)

            val response = _removeFromCartUseCase.execute(referenceReq)

            response.fold(
                right = {

                },
                left = {
//                    _state.update {
//                        it.copy(
//                            cartItems = _state.value.cartItems.apply {
//                                add(indexOfItem, item)
//                            }
//                        )
//                    }
                }
            )
        }
    }

    private fun _increaseQuantity(item: CartItemDetails) {

        if (item.productDetails == null) return

        viewModelScope.launch {
            val cartItemReq = CartItemReq(
                quantity = item.quantity!! + 1,
                cartItemId = item.productDetails!!.productReference!!.id
            )
            _addToCartUseCase.execute(cartItemReq)

        }

    }

    private fun _decreaseQuantity(item: CartItemDetails) {

        if (item.productDetails == null) return
        viewModelScope.launch {
            val cartItemReq = CartItemReq(
                quantity = item.quantity!! - 1,
                cartItemId = item.productDetails!!.productReference!!.id
            )
            _addToCartUseCase.execute(cartItemReq)

        }
    }


    override fun onCleared() {
        _cartReq.cancelListener()
        _favoritesJob?.cancel()
        super.onCleared()
    }

    private fun ProductDetails.handleFavorites(): ProductDetails {
        return if (_favorites.isInFavorites(this.productReference!!))
            this.copy(isFavorite = true)
        else this.copy(isFavorite = false)
    }

    private fun _updateProductFavoriets() {
        _state.update {
            it.copy(
                cartItems = it.cartItems.map { product ->
                    product.copy(
                        productDetails = product.productDetails?.handleFavorites()
                    )
                }.toMutableSet()
            )
        }
    }
}