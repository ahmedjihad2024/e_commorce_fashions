package com.example.e_commorce_fashions.presentation.views.personal_details.view_state

import android.net.Uri

sealed interface PersonalDetailsEvent {
    data object GetUserDetails : PersonalDetailsEvent
    data class UpdateProfileDetails(
        val name: String?,
        val email: String?,
        val pictureUrl: Uri?,
    ): PersonalDetailsEvent
}