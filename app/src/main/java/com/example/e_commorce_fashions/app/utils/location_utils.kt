package com.example.e_commorce_fashions.app.utils

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationUtils(val context: Context) {


    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var _locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(callback: (latLng: LatLng) -> Unit) {
        _locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    callback(
                        LatLng(
                             it.latitude,
                             it.longitude
                        )
                    )
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest, _locationCallback!!, Looper.getMainLooper())
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY) // Set the priority level
            .setMaxUpdateAgeMillis(1000*10) // Set the maximum time since the last known location can be used
            .build()

        val cancellationTokenSource = CancellationTokenSource()
        return  _fusedLocationClient?.getCurrentLocation(locationRequest, cancellationTokenSource.token)?.await()
    }

    fun closeLocationUpdate(){
        if(_locationCallback == null) return
        _fusedLocationClient.removeLocationUpdates(_locationCallback!!)
    }


    fun hasLocationPermission(): Boolean =
        (ContextCompat.checkSelfPermission(
            context,
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
//                && (ContextCompat.checkSelfPermission(
//            context,
//            permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED)


    fun reverseGeocodeLocation(latLng: LatLng): Address?{
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return if(addresses?.isNotEmpty() == true){
            addresses[0]
        }else {
            null
        }
    }

    fun checkIfLocationIsEnabled(onResult: (Boolean) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        onResult(isGpsEnabled)
    }

    fun promptUserToEnableLocation(context: Context) {
        Toast.makeText(context, "Please enable location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(context, intent, null)
    }

}