package com.example.e_commorce_fashions.presentation.views.search.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.SearchReq
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.AddFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.FavoritesListenerUseCase
import com.example.e_commorce_fashions.domain.use_case.GetCategoriesUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFavoriteUseCase
import com.example.e_commorce_fashions.domain.use_case.SearchUseCase
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewStateFactory(private val repository: Repository = Di.repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewState::class.java)) {
            return SearchViewState(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SearchViewState(repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var _lastDocument: DocumentSnapshot? = null
    private var _favoritesJob: Job? = null
    private var _favorites = Favorites(emptyList())
    private var _query: String? = null
    private var _minPrice: Int? = null
    private var _maxPrice: Int? = null
    private var _categoryIndex: Int? = null
    private var _loadState: LoadableListState? = null

    private val _getCategories = GetCategoriesUseCase(repository)
    private val _favoritesUseCase = FavoritesListenerUseCase(repository)
    private val _search = SearchUseCase(repository)
    private val _addFavoriteUseCase = AddFavoriteUseCase(repository)
    private val _removeFavoriteUseCase = RemoveFavoriteUseCase(repository)


    init {
        viewModelScope.launch {
            _favoritesJob?.cancel()
            _fetchFavorites()
        }
    }

    private suspend fun _fetchFavorites() {
        val result = _favoritesUseCase.execute {
            _favorites = it
        }

        result.fold(
            left = { },
            right = { right ->
                _favoritesJob = right
            }
        )
    }

    fun onEvent(event: SearchEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchEvent.LoadMore -> {
                    if (_query != null) {
                        _search(
                            _query!!,
                            _minPrice!!,
                            _maxPrice!!,
                            _categoryIndex!!,
                            true,
                            _loadState
                        )
                    }
                }

                is SearchEvent.RefreshPage -> {
                    if (_query != null) {
                        _search(
                            _query!!,
                            _minPrice!!,
                            _maxPrice!!,
                            _categoryIndex!!,
                            false,
                            _loadState
                        )
                    }
                }

                is SearchEvent.AddFavorite -> {
                    _addFavorites(event.productDetails)
                }

                is SearchEvent.RemoveFavorite -> {
                    _removeFavorite(event.productDetails)
                }

                is SearchEvent.SearchProduct -> {

                    _query = event.query
                    _minPrice = event.minPrice
                    _maxPrice = event.maxPrice
                    _categoryIndex = event.categoryIndex
                    _loadState = event.loadState

                    _search(
                        event.query,
                        event.minPrice,
                        event.maxPrice,
                        event.categoryIndex,
                        false,
                        event.loadState
                    )
                }

                is SearchEvent.GetCategories -> {
                    _getCategories()
                }

                else -> {}
            }
        }
    }

    private suspend fun _getCategories() {
        val result = _getCategories.execute(Unit)

        result.fold(
            left = {},
            right = { right ->
                _state.value = _state.value.copy(
                    categories = right.categories
                )
            }
        )
    }


    private suspend fun _search(
        query: String,
        minPrice: Int,
        maxPrice: Int,
        categoryIndex: Int,
        loadMore: Boolean = false,
        loadState: LoadableListState? = null
    ) {

        if (!loadMore) {
            _lastDocument = null
            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }
        } else {
            loadState?.setLoadingMore()
        }

        val searchRequest = SearchReq(
            about = query,
            minPrice = minPrice,
            maxPrice = maxPrice,
            lastDocument = _lastDocument,
            categoryReference = if (categoryIndex == -1) null else _state.value.categories[categoryIndex].reference,
            limit = 10
        )

        val result = _search.execute(searchRequest)

        result.fold(
            left = { left ->
                if (!loadMore) {
                    loadState?.reset()
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                } else {
                    loadState?.setLoadError()
                }
            },
            right = { right ->
                _lastDocument = right.lastDocument

                if (right.products.isEmpty() && (_state.value.products.isEmpty() || !loadMore)) {
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = "No products found"
                        )
                    }
                } else if (!loadMore) {
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

}