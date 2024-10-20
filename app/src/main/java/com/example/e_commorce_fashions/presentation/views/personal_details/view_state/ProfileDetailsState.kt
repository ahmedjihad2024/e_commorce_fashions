package com.example.e_commorce_fashions.presentation.views.personal_details.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.UserDetails

data class PersonalDetailsState(
    val requestState: RequestState = RequestState.IDLE,
    val errorMessage: String = "",
    val userDetails: UserDetails? = null,
    val updatingState: RequestState = RequestState.IDLE,
)