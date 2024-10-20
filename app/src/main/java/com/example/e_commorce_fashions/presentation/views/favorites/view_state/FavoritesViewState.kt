package com.example.e_commorce_fashions.presentation.views.favorites.view_state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.FavoritesReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.GetFavoritesUseCase
import com.example.e_commorce_fashions.domain.use_case.RemoveFavoriteUseCase
import com.example.e_commorce_fashions.presentation.common.LoadableListState
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FavoritesViewStateFactory(
    private val repository: Repository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewState(repository) as T
    }
}

class FavoritesViewState(
    val repository: Repository
): ViewModel(){

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    private val _getFavorites = GetFavoritesUseCase(repository)
    private val _removeFavorites = RemoveFavoriteUseCase(repository)

    private var _lastDocument:  DocumentSnapshot? = null

    fun onEvent(event: FavoritesEvent){
        when(event){
            is FavoritesEvent.RemoveFavorite -> {
                _removeFavorite(event.productDetails)
            }
            is FavoritesEvent.GetFavorites -> {
                _getFavorites(true, event.refresher)
            }
            is FavoritesEvent.LoadMore -> {
                _getFavorites(false, event.refresher)
            }

            is FavoritesEvent.RemoveFavoriteLocalList -> {
                _state.update {
                    it.copy(
                        items = it.items.filter { product -> product.productReference!!.id != event.productDetails.productReference!!.id }
                    )
                }
            }
        }
    }

    private fun _getFavorites(
        refresh: Boolean = false,
        refresher: LoadableListState? = null
    ) {
        viewModelScope.launch {
            if(refresh){
                _lastDocument = null
                _state.update {
                    it.copy(
                        requestState = RequestState.LOADING
                    )
                }
            }else{
                refresher?.setLoadingMore()
            }

            val result = _getFavorites.execute(
                FavoritesReq(_lastDocument, 10)
            )

            result.fold(
                left = { left ->
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                },
                right = { right ->
                    val key = right.lastDocument
                    _lastDocument = key ?: _lastDocument

                    if(refresh){
                        refresher?.setNotRefreshing()
                        _state.update {
                            it.copy(
                                requestState = RequestState.SUCCESS,
                                items = right.products
                            )
                        }
                    }else {
                        if (key == null) {
                            refresher?.setLoadNoMore()
                        } else {
                            refresher?.setLoadingComplete()
                        }
                        _state.update {
                            it.copy(
                                requestState = RequestState.SUCCESS,
                                items = it.items + right.products
                            )
                        }
                    }

                }
            )

        }
    }

    private fun _removeFavorite(productDetails: ProductDetails){
        viewModelScope.launch {
            val reference = ItemReferenceReq(productDetails.productReference!!)
            val response = _removeFavorites.execute(reference)

            response.fold(
                right = {
                    _state.update {
                        it.copy(
                            items = it.items.filter { product -> product.productReference!! != productDetails.productReference }
                        )
                    }
                },
                left = {

                }
            )
        }
    }

}