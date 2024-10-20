package com.example.e_commorce_fashions.presentation.views.product_details.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.repository.RepositoryImpl
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.data.requests.CartReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceWithOnDataChangeReq
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.AddFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.AddToCartUseCase
import com.example.e_commorce_fashions.domain.use_case.ListenToCartItemUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFromCartUseCase
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesEvent
import com.example.e_commorce_fashions.presentation.views.favorites.view_state.FavoritesViewState
import com.google.firebase.Firebase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewStateFactory(
    val repository: Repository,
    val product: ProductDetails?,
    val favoritesViewModel: FavoritesViewState?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (
            modelClass.isAssignableFrom(ProductDetailsViewState::class.java)
        ) return ProductDetailsViewState(
            repository = repository,
            product = product,
            favoritesViewModel = favoritesViewModel
        ) as T
        else throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProductDetailsViewState(val repository: Repository, val product: ProductDetails?, val favoritesViewModel: FavoritesViewState? = null) :
    ViewModel() {
    private val _state = MutableStateFlow(ProductDetailsUiState(product))
    private val _toastState = MutableSharedFlow<String>()

    val state: StateFlow<ProductDetailsUiState> get() = _state
    val stateToast: SharedFlow<String> get() = _toastState

    private val _addFavoriteUseCase = AddFavoriteUseCase(repository)
    private val _removeFavoriteUseCase = RemoveFavoriteUseCase(repository)
    private val _addToCartUseCase = AddToCartUseCase(repository)
    private val _removeFromCartUseCase = RemoveFromCartUseCase(repository)
    private val _listenToCartItemUseCase = ListenToCartItemUseCase(repository)

    private var itemReferenceReq: ItemReferenceWithOnDataChangeReq? = null

    init {

        _listenToItemInCart()

    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.AddFavorite -> {
                _addFavorites()
            }

            is ProductDetailsEvent.AddToCart -> {
                _addToCart()
            }

            is ProductDetailsEvent.RemoveFavorite -> {
                _removeFavorite()
            }

            is ProductDetailsEvent.RemoveFromCart -> {
                _removeFromCart()
            }

            is ProductDetailsEvent.DecreaseQuantity -> {
                _decreaseQuantity()
            }

            is ProductDetailsEvent.IncreaseQuantity -> {
                _increaseQuantity()
            }

            is ProductDetailsEvent.SelectedColor -> {
                _selectColor(event.color)
            }

            is ProductDetailsEvent.SelectedSize -> {
                _selectSize(event.size)
            }
        }
    }


    private fun _listenToItemInCart() {
        viewModelScope.launch {
            itemReferenceReq = ItemReferenceWithOnDataChangeReq(
                itemReferenceReq = ItemReferenceReq(_state.value.product?.productReference!!),
                onDataChange = { data ->
                    if (data != null) {
                        _state.update {
                            it.copy(
                                isInCart = true,
                                quantity = data.quantity ?: 1,
                                selectedColor = data.color ?: "",
                                selectedSize = data.size ?: ""
                            )
                        }
                    }
                },
                onCanceled = {

                }
            )

            _listenToCartItemUseCase.execute(itemReferenceReq!!)

        }
    }

    private fun _selectColor(color: String) {

        val lastSelectedColor = _state.value.selectedColor

        _state.update {
            it.copy(
                selectedColor = color
            )
        }

        if (_state.value.isInCart) {
            viewModelScope.launch {
                val cartItemReq = CartItemReq(
                    color = _state.value.selectedColor,
                    cartItemId = _state.value.product?.productReference!!.id
                )
                val response = _addToCartUseCase.execute(cartItemReq)

                response.fold(
                    right = {
                        launch {
                            _toastState.emit("Cart Updated")
                        }
                    },
                    left = { left ->
                        _state.update {
                            it.copy(
                                selectedColor = lastSelectedColor
                            )
                        }
                    }
                )
            }
        }
    }

    private fun _selectSize(size: String) {
        val lastSelectedSize = _state.value.selectedSize

        _state.update {
            it.copy(
                selectedSize = size
            )
        }

        if (_state.value.isInCart) {
            viewModelScope.launch {
                val cartItemReq = CartItemReq(
                    size = _state.value.selectedSize,
                    cartItemId = _state.value.product?.productReference!!.id
                )
                val response = _addToCartUseCase.execute(cartItemReq)

                response.fold(
                    right = {
                        launch {
                            _toastState.emit("Cart Updated")
                        }
                    },
                    left = { left ->
                        _state.update {
                            it.copy(
                                selectedSize = lastSelectedSize
                            )
                        }
                    }
                )
            }
        }
    }

    private fun _decreaseQuantity() {
        _state.update {
            it.copy(
                quantity = it.quantity - 1
            )
        }

        if (_state.value.isInCart) {
            viewModelScope.launch {
                val cartItemReq = CartItemReq(
                    quantity = _state.value.quantity,
                    cartItemId = _state.value.product?.productReference!!.id
                )

                val response = _addToCartUseCase.execute(cartItemReq)

                response.fold(
                    right = {
                        launch {
                            _toastState.emit("Cart Updated")
                        }
                    },
                    left = { left ->
                        _state.update {
                            it.copy(
                                quantity = it.quantity + 1
                            )
                        }
                    }
                )

            }
        }
    }

    private fun _removeFromCart() {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    isInCart = false
                )
            }

            val referenceReq = ItemReferenceReq(_state.value.product?.productReference!!)

            val response = _removeFromCartUseCase.execute(referenceReq)

            response.fold(
                right = {

                },
                left = {
                    _state.update {
                        it.copy(
                            isInCart = true
                        )
                    }
                }
            )
        }
    }

    private fun _increaseQuantity() {
        _state.update {
            it.copy(
                quantity = it.quantity + 1
            )
        }

        if (_state.value.isInCart) {
            viewModelScope.launch {
                val cartItemReq = CartItemReq(
                    quantity = _state.value.quantity,
                    cartItemId = _state.value.product?.productReference!!.id
                )
                val response = _addToCartUseCase.execute(cartItemReq)

                response.fold(
                    right = {
                        launch {
                            _toastState.emit("Cart Updated")
                        }
                    },
                    left = { left ->
                        _state.update {
                            it.copy(
                                quantity = it.quantity - 1
                            )
                        }
                    }
                )
            }
        }
    }

    private fun _removeFavorite() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    product = it.product?.copy(isFavorite = false)
                )
            }

            val reference = ItemReferenceReq(_state.value.product?.productReference!!)
            val response = _removeFavoriteUseCase.execute(reference)

            response.fold(
                right = {
                    favoritesViewModel?.onEvent(FavoritesEvent.RemoveFavoriteLocalList(_state.value.product!!))
                },
                left = {
                    _state.update {
                        it.copy(
                            product = it.product?.copy(isFavorite = true)
                        )
                    }
                }
            )
        }
    }

    private fun _addFavorites() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    product = it.product?.copy(isFavorite = true)
                )
            }

            val reference = ItemReferenceReq(_state.value.product?.productReference!!)
            val response = _addFavoriteUseCase.execute(reference)

            response.fold(
                right = {

                },
                left = {
                    _state.update {
                        it.copy(
                            product = it.product?.copy(isFavorite = false)
                        )
                    }
                }
            )
        }
    }

    private fun _addToCart() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isInCart = true
                )
            }

            val cartItemReq = CartItemReq(
                productReference = _state.value.product?.productReference!!,
                quantity = _state.value.quantity,
                color = _state.value.selectedColor,
                size = _state.value.selectedSize,
                cartItemId = _state.value.product?.productReference!!.id
            )
            val response = _addToCartUseCase.execute(cartItemReq)

            response.fold(
                right = {

                },
                left = { left ->
//                    Log.d("TAG", left.userMessage)
                    _state.update {
                        it.copy(
                            isInCart = false
                        )
                    }
                }
            )

        }
    }

    override fun onCleared() {
        itemReferenceReq?.cancelListener()
        super.onCleared()
    }
}