package com.example.e_commorce_fashions.presentation.views.home.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ProductsReq
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.AddFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.FavoritesListenerUseCase
import com.example.e_commorce_fashions.domain.use_case.FetchNewArrivalProductsUseCase
import com.example.e_commorce_fashions.domain.use_case.FetchPopularProductsUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFavoriteUseCase
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewStateFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewState::class.java)) {
            return HomeViewState(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HomeViewState(repository: Repository) : ViewModel() {
    
    private val _statePage = MutableStateFlow(HomeUiState.PageState())
    private val _stateNewArrival = MutableStateFlow(HomeUiState.NewArrivalState())
    private val _statePopular = MutableStateFlow(HomeUiState.PopularState())
    private val _snackBar = MutableSharedFlow<String>()

    val statePage: StateFlow<HomeUiState.PageState> = _statePage.asStateFlow()
    val stateNewArrival: StateFlow<HomeUiState.NewArrivalState> = _stateNewArrival.asStateFlow()
    val statePopular: StateFlow<HomeUiState.PopularState> = _statePopular.asStateFlow()
    val snackBar: Flow<String> = _snackBar.asSharedFlow()

    private val _newArrivalProducts = FetchNewArrivalProductsUseCase(repository)
    private val _popularProducts = FetchPopularProductsUseCase(repository)
    private val _favoritesUseCase = FavoritesListenerUseCase(repository)
    private val _addFavoriteUseCase = AddFavoriteUseCase(repository)
    private val _removeFavoriteUseCase = RemoveFavoriteUseCase(repository)

    private var _lastNewArrivalDocument: DocumentSnapshot? = null
    fun lastNewArrivalDocument(): DocumentSnapshot? = _lastNewArrivalDocument
    
    private var lastPopularDocument: DocumentSnapshot? = null

    private var _favoritesJob: Job? = null
    private var _favorites = Favorites(emptyList())

    init {
        _loadPageData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadNewArrivalsProducts -> {
                viewModelScope.launch {
                    _fetchNewArrivalProducts(loadState = event.loadState)
                }
            }

            is HomeEvent.LoadPopularProducts -> {
                viewModelScope.launch {
                    _fetchPopularProducts(loadState = event.loadState)
                }
            }

            is HomeEvent.ReloadPage -> {
                _loadPageData()
            }

            is HomeEvent.AddFavorite -> {
                _addFavorites(event.productDetails)
            }

            is HomeEvent.RemoveFavorite -> {
                _removeFavorite(event.productDetails)
            }

            else -> {}
        }
    }

    private fun _loadPageData() {

//        _state.update {
//            it.copy(
//                newArrivalState = RequestState.LOADING,
//                popularState = RequestState.LOADING
//            )
//        }
//        viewModelScope.launch {
//            _fetchNewArrivalProducts(refresh = true).also {
//                if(!it) return@launch
//            }
//
//            _fetchPopularProducts(refresh = true).also {
//                if(!it) return@launch
//            }
//        }
        
        if(_statePage.value.requestState != RequestState.SUCCESS){
            _statePage.update {
                it.copy(
                    requestState = RequestState.SUCCESS,
                )
            }
        }

        viewModelScope.launch {
            _favoritesJob?.cancel()
            _fetchFavorites()
        }

        viewModelScope.launch {
            _fetchNewArrivalProducts(refresh = true)
        }

        viewModelScope.launch {
            _fetchPopularProducts(refresh = true)
        }
    }

    private suspend fun _fetchFavorites() {
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

    private suspend fun _fetchNewArrivalProducts(
        refresh: Boolean = false,
        loadState: LoadableListState? = null
    ): Boolean {
        if (refresh) {
            _lastNewArrivalDocument = null
            _stateNewArrival.update {
                it.copy(
                    newArrivalState = RequestState.LOADING,
                )
            }
        } else {
            loadState?.setLoadingMore()
        }

        val result = _newArrivalProducts.execute(
            ProductsReq(
                limit = 7,
                lastDocument = _lastNewArrivalDocument
            )
        )
        return result.fold(
            left = { left ->
                if (refresh) {
                    _statePage.update {
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

                _lastNewArrivalDocument = right.lastDocument ?: _lastNewArrivalDocument

                if (refresh) {
                    _stateNewArrival.update {
                        it.copy(
                            newArrivalProducts = right.products.map { product -> product.handleFavorites() },
                            newArrivalState = RequestState.SUCCESS
                        )
                    }
                } else {
                    if (right.lastDocument == null) {
                        loadState?.setLoadNoMore()
                    } else {
                        loadState?.setLoadingComplete()
                    }
                    _stateNewArrival.update {
                        it.copy(
                            newArrivalProducts = it.newArrivalProducts + right.products.map { product -> product.handleFavorites() },
                            newArrivalState = RequestState.SUCCESS
                        )
                    }
                }

                return@fold true
            }
        )
    }

    private suspend fun _fetchPopularProducts(
        refresh: Boolean = false,
        loadState: LoadableListState? = null
    ): Boolean {
        if (refresh) {
            lastPopularDocument = null
            _statePopular.update {
                it.copy(
                    popularState = RequestState.LOADING
                )
            }
        } else {
            loadState?.setLoadingMore()
        }

        val result =
            _popularProducts.execute(ProductsReq(limit = 7, lastDocument = lastPopularDocument))
        return result.fold(
            left = { left ->
                if (refresh) {
                    _statePage.update {
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
                // Log.d("my-data" , "fetchNewArrivalProducts: ${right.products}")
                lastPopularDocument = right.lastDocument ?: lastPopularDocument
                if (refresh) {
                    _statePopular.update {
                        it.copy(
                            popularProducts = right.products.map { product -> product.handleFavorites() },
                            popularState = RequestState.SUCCESS
                        )
                    }
                } else {
                    if (right.lastDocument == null) {
                        loadState?.setLoadNoMore()
                    } else {
                        loadState?.setLoadingComplete()
                    }
                    _statePopular.update {
                        it.copy(
                            popularProducts = it.popularProducts + right.products.map { product -> product.handleFavorites() },
                            popularState = RequestState.SUCCESS
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
        _stateNewArrival.update {
            it.copy(
                newArrivalProducts = it.newArrivalProducts.map { product ->
                    if (product.productReference == productDetails.productReference) {
                        product.copy(isFavorite = isFavorites)
                    } else {
                        product
                    }
                }
            )
        }
    }


    private fun _updateProductFavoriets(){
        _stateNewArrival.update {
            it.copy(
                newArrivalProducts = it.newArrivalProducts.map { product -> product.handleFavorites() }
            )
        }

        _statePopular.update {
            it.copy(
                popularProducts = it.popularProducts.map { product -> product.handleFavorites() }
            )
        }
    }

    private fun ProductDetails.handleFavorites(): ProductDetails {
        return if(_favorites.isInFavorites(this.productReference!!))
            this.copy(isFavorite = true)
        else this.copy(isFavorite = false)
    }

    override fun onCleared() {
        super.onCleared()
        _favoritesJob?.cancel()
    }

}