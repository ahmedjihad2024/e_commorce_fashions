package com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state

import com.example.e_commorce_fashions.app.utils.RequestState

data class UiState(
    val requestState: RequestState = RequestState.IDLE,
    val errorMessage: String = ""
)