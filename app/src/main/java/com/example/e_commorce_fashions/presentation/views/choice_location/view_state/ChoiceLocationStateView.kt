package com.example.e_commorce_fashions.presentation.views.choice_location.view_state

import android.content.Context
import android.location.Location
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.LocationUtils
import com.example.e_commorce_fashions.domain.models.ProductDetails
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.presentation.views.product_details.view_state.ProductDetailsViewState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect


class ChoiceLocationStateViewFactory(val locationUtils: LocationUtils) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (
            modelClass.isAssignableFrom(ChoiceLocationStateView::class.java)
        ) return ChoiceLocationStateView(locationUtils) as T
        else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class ChoiceLocationStateView(private val locationUtils: LocationUtils): ViewModel() {
    private val _state = MutableStateFlow(ChoiceLocationState())
    val state: StateFlow<ChoiceLocationState> = _state.asStateFlow()

    fun  onEvent(event: ChoiceLocationEvent) {
        when(event){
            ChoiceLocationEvent.StartLocationUpdate -> {
                _startLocationUpdate()
            }
            ChoiceLocationEvent.StopLocationUpdate -> {
                _endLocationUdpate()
            }

            ChoiceLocationEvent.GetCurrentLocation -> {
                _getCurrentLocation()
            }

            is ChoiceLocationEvent.UpdateLocation -> {
                _updateLocation(event.latLng)
            }
        }
    }

    private fun _startLocationUpdate(){
        locationUtils.requestLocationUpdates { location ->
            val address = locationUtils.reverseGeocodeLocation(location)
            _state.update {
                it.copy(
                    location = location,
                    locationAddress = address?.getAddressLine(0),
                    locality = address?.locality,
                    address = address
                )
            }
        }
    }

    private fun _endLocationUdpate(){
        locationUtils.closeLocationUpdate()
    }

    private fun _updateLocation(latLng: LatLng){
        val address = locationUtils.reverseGeocodeLocation(latLng) ?: return
        _state.update {
            it.copy(
                location = latLng,
                locationAddress = address.getAddressLine(0),
                locality = address.locality,
                address = address
            )
        }
    }


    private fun _getCurrentLocation(){
        viewModelScope.launch {
            val location = locationUtils.getCurrentLocation() ?: return@launch
            val address = locationUtils.reverseGeocodeLocation(LatLng(location.latitude, location.longitude))
            _state.update {
                it.copy(
                    location = LatLng(location.latitude, location.longitude),
                    locationAddress = address?.getAddressLine(0),
                    locality = address?.locality,
                    address = address
                    )
            }
        }
    }

    suspend fun getCurrentLocation(): Location?{
        return viewModelScope.async { locationUtils.getCurrentLocation() }.await()
    }

    fun hasLocationPermission(): Boolean = locationUtils.hasLocationPermission()

    fun checkIfLocationIsEnabled(onResult: (Boolean) -> Unit) {
        locationUtils.checkIfLocationIsEnabled(onResult)
    }

    fun promptUserToEnableLocation(context: Context) {
        locationUtils.promptUserToEnableLocation(context)
    }

    override fun onCleared() {
        _endLocationUdpate()
        super.onCleared()
    }
}