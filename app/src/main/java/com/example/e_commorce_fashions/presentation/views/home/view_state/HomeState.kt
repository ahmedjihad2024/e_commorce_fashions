package com.example.e_commorce_fashions.presentation.views.home.view_state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.ProductDetails
import kotlinx.coroutines.flow.MutableSharedFlow


@Immutable
sealed class HomeUiState {

    @Immutable
    data class PageState(
        val requestState: RequestState = RequestState.SUCCESS,
        val errorMessage: String? = null
    ): HomeUiState()

    @Immutable
    data class NewArrivalState(
        val newArrivalState: RequestState = RequestState.LOADING,
        val newArrivalProducts: List<ProductDetails> = emptyList(),
    ): HomeUiState()

    @Immutable
    data class PopularState(
        val popularState: RequestState = RequestState.LOADING,
        val popularProducts: List<ProductDetails> = emptyList(),
    ): HomeUiState()

}


// Old way and i still love it

//@Immutable
//data class UiState(
//    val requestState: RequestState = RequestState.SUCCESS,
//    val newArrivalState: RequestState = RequestState.LOADING,
//    val popularState: RequestState = RequestState.LOADING,
//    val newArrivalProducts: List<ProductDetails> = emptyList(),
//    val popularProducts: List<ProductDetails> = emptyList(),
//    val errorMessage: String? = null
//)