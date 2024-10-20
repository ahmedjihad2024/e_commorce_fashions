package com.example.e_commorce_fashions.presentation.views.profile.view_state

sealed interface ProfileEvent{
    data object GetUserDetails: ProfileEvent
    data object Logout: ProfileEvent
}