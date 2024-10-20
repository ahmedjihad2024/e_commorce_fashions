package com.example.e_commorce_fashions.presentation.views.choice_location.view_state

import com.google.android.gms.maps.model.LatLng

sealed interface ChoiceLocationEvent{
    data object StartLocationUpdate: ChoiceLocationEvent
    data object StopLocationUpdate: ChoiceLocationEvent
    data object  GetCurrentLocation: ChoiceLocationEvent
    data class UpdateLocation(val latLng: LatLng) : ChoiceLocationEvent
}