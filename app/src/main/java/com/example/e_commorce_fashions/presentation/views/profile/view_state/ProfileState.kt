package com.example.e_commorce_fashions.presentation.views.profile.view_state

import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.domain.models.UserDetails

data class ProfileState(
    val requestState: RequestState = RequestState.SUCCESS,
    val errorMessage: String = "",
    val userDetails: UserDetails? = null,
    val isLogout: Boolean = false
)