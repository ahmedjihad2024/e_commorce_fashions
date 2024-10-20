package com.example.e_commorce_fashions.presentation.views.view_all.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ProductsReq
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.AddFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.FavoritesListenerUseCase
import com.example.e_commorce_fashions.domain.use_case.FetchNewArrivalProductsUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFavoriteUseCase
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import com.example.e_commorce_fashions.presentation.views.layout.view.compose.ViewAllProductsScreenData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.AutoCloseable


class ViewAllProductsStateViewFactory(
    private val repository: Repository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewAllProductsStateView::class.java)) {
            return ViewAllProductsStateView(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ViewAllProductsStateView(private val repository: Repository, private val di: Di = Di): ViewModel() {
    private var _isInit = false

    private val _state = MutableStateFlow(ViewAllProductsState())
    val state get() = _state


    private val _newArrivalProducts = FetchNewArrivalProductsUseCase(repository)
    private val _favoritesUseCase = FavoritesListenerUseCase(repository)
    private val _addFavoriteUseCase = AddFavoriteUseCase(repository)
    private val _removeFavoriteUseCase = RemoveFavoriteUseCase(repository)

    private var _lastDocument: DocumentSnapshot? = null

    private var _favoritesJob: Job? = null
    private var _favorites = Favorites(emptyList())


    fun onEvent(event: ViewAllProductsEvent) {
        viewModelScope.launch {
            when(event){
                is ViewAllProductsEvent.LoadPopularProducts -> {
                    _fetchNewArrivalProducts(loadState = event.loadState)
                }
                is ViewAllProductsEvent.AddFavorite -> {
                    _addFavorites(event.productDetails)
                }
                is ViewAllProductsEvent.RemoveFavorite -> {
                    _removeFavorite(event.productDetails)
                }
                is ViewAllProductsEvent.Init -> {
                    _init()
                }
                is ViewAllProductsEvent.Refresh -> {
                    _fetchNewArrivalProducts(refresh = true, loadState = event.loadState)
                }
            }
        }
    }


    private fun _init(){
        if(!_isInit){
            _isInit = true
            viewModelScope.launch {
                _favoritesJob?.cancel()
                _fetchFavorites()
                _loadPage()
            }
        }
    }

    private suspend fun _loadPage() {
        val sharedData: ViewAllProductsScreenData? = di.sharedData.value.getData("view-all-products") as ViewAllProductsScreenData?
        if(sharedData == null || sharedData.products.isEmpty()){
            _fetchNewArrivalProducts(refresh = true)
        }else{
            _lastDocument = sharedData.lastDocumentSnapshot
            _state.update {
                it.copy(products = sharedData.products, requestState = RequestState.SUCCESS )
            }
        }
    }


    private suspend fun _fetchFavorites() {
        val result = _favoritesUseCase.execute {
            Log.d("my-data", "fetchFavorites: ${it.favorites.size}")
            _favorites = it
        }

        result.fold(
            left = { },
            right = { right ->
                _favoritesJob = right
            }
        )
    }

    private suspend fun _fetchNewArrivalProducts(
        refresh: Boolean = false,
        loadState: LoadableListState? = null
    ): Boolean {
        if (refresh) {
            _lastDocument = null
            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }
        } else {
            loadState?.setLoadingMore()
        }

        val result = _newArrivalProducts.execute(
            ProductsReq(
                limit = 7,
                lastDocument = _lastDocument
            )
        )

        return result.fold(
            left = { left ->
                if (refresh) {
                    loadState?.reset()
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(left.userMessage),
                            errorMessage = left.userMessage
                        )
                    }
                } else {
                    loadState?.setLoadError()
                }

                return@fold false
            },
            right = { right ->
                _lastDocument = right.lastDocument ?: _lastDocument

                if (refresh) {
                    loadState?.reset()
                    _state.update {
                        it.copy(
                            products = right.products.map { product -> product.handleFavorites() },
                            requestState = RequestState.SUCCESS
                        )
                    }
                } else {
                    if (right.lastDocument == null) {
                        loadState?.setLoadNoMore()
                    } else {
                        loadState?.setLoadingComplete()
                    }
                    _state.update {
                        it.copy(
                            products = it.products + right.products.map { product -> product.handleFavorites() },
                            requestState = RequestState.SUCCESS
                        )
                    }
                }

                return@fold true
            }
        )
    }

    private fun _removeFavorite(productDetails: ProductDetails){
        viewModelScope.launch {
            _updateFavoriteState(productDetails, false)

            val reference = ItemReferenceReq(productDetails.productReference!!)
            val response = _removeFavoriteUseCase.execute(reference)

            response.fold(
                right = {

                },
                left = {
                    _updateFavoriteState(productDetails, true)
                }
            )
        }
    }

    private fun _addFavorites(productDetails: ProductDetails) {
        viewModelScope.launch {
            _updateFavoriteState(productDetails, true)

            val reference = ItemReferenceReq(productDetails.productReference!!)
            val response = _addFavoriteUseCase.execute(reference)

            response.fold(
                right = {

                },
                left = {
                    _updateFavoriteState(productDetails, false)
                }
            )
        }
    }

    private fun _updateFavoriteState(productDetails: ProductDetails, isFavorites: Boolean) {
        _state.update {
            it.copy(
                products = it.products.map { product ->
                    if (product.productReference == productDetails.productReference) {
                        product.copy(isFavorite = isFavorites)
                    } else {
                        product
                    }
                }
            )
        }
    }


    private fun ProductDetails.handleFavorites(): ProductDetails {
        return if(_favorites.isInFavorites(this.productReference!!))
            this.copy(isFavorite = true)
        else this
    }


    override fun onCleared() {
        super.onCleared()
        _favoritesJob?.cancel()
    }
}