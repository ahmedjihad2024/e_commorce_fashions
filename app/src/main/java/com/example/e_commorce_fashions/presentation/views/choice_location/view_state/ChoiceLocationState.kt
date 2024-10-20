package com.example.e_commorce_fashions.presentation.views.choice_location.view_state

import android.location.Address
import com.google.android.gms.maps.model.LatLng


data class ChoiceLocationState(
    val location: LatLng? = null,
    val locationAddress: String? = null,
    val locality: String? = null,
    val address: Address? = null
)