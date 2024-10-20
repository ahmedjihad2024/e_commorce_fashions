package com.example.e_commorce_fashions.presentation.views.choice_location.views

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.MainActivity
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.LocationUtils
import com.example.e_commorce_fashions.presentation.common.statusBarHeight
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.choice_location.view_state.ChoiceLocationEvent
import com.example.e_commorce_fashions.presentation.views.choice_location.view_state.ChoiceLocationStateView
import com.example.e_commorce_fashions.presentation.views.choice_location.view_state.ChoiceLocationStateViewFactory
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Composable
fun ChoiceLocationView(
    navController: () -> NavHostController
) {

    val amount = Di.sharedData.value.getData<Double>("amount")!!
    val context = LocalContext.current
    val uiState = viewModel<ChoiceLocationStateView>(
        factory = ChoiceLocationStateViewFactory(
            locationUtils = LocationUtils(context)
        )
    )
    val state = uiState.state.collectAsState()

    val scope = rememberCoroutineScope()
    val googleMapOptions by rememberSaveable { mutableStateOf(GoogleMapOptions()) }
    var mapProperties by remember { mutableStateOf(MapProperties()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(26.820553, 30.802498), 4f)
    }

    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                scope.launch {
                    val location: Location? = uiState.getCurrentLocation()
                    if (location != null) {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(
                                    LatLng(location.latitude, location.longitude),
                                    15f
                                )
                            ), 500
                        )
                        uiState.onEvent(
                            ChoiceLocationEvent.UpdateLocation(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        )
                    }
                }
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                        || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (rationaleRequired) { // show why we need to access
                    Toast.makeText(context, "Location Permission Is Required", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Enable Permission Manually",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    )

    var launched by rememberSaveable{ mutableStateOf(false) }
    LaunchedEffect(launched) {
        if(!launched){
            uiState.checkIfLocationIsEnabled {
                if(!it) uiState.promptUserToEnableLocation(context)
            }
            mapProperties = mapProperties.copy(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
            if (uiState.hasLocationPermission()) {
                val location: Location? = uiState.getCurrentLocation()
                if (location != null) {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                17f
                            )
                        ), 500
                    )
                    uiState.onEvent(
                        ChoiceLocationEvent.UpdateLocation(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
            } else {
                requestPermission.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            launched = true
        }
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow {
            cameraPositionState.isMoving
        }.stateIn(scope, started = SharingStarted.WhileSubscribed(5000L), initialValue = true)
            .collectLatest { isMoving: Boolean ->
                if(!isMoving) uiState.onEvent(ChoiceLocationEvent.UpdateLocation(cameraPositionState.position.target))
            }
    }

    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                googleMapOptions
            },
            properties = mapProperties,
            uiSettings = MapUiSettings(
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false
            ),
            onMapClick = {
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                it,
                                cameraPositionState.position.zoom
                            )
                        ), 500
                    )
                }
            }
        ) {

        }

        CenteredMarker(state.value.locality)

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusBarHeight)
                .padding(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                navController().navigateUp()
                            }
                            .background(Scheme.primary)
                            .size(55.dp)
                            .padding(15.dp)
                            .wrapContentSize(Alignment.Center),

                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.ic_arrow_back,),
                            contentDescription = "",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(Scheme.onPrimary)
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        text = stringResource(R.string.choose_delivery_location),
                        style = TitleSmall.copy(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Selected location address
                if (state.value.locationAddress != null) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(15.dp)
                    ) {
                        Text(
                            text = state.value.locationAddress!!,
                            style = TitleSmall.copy(
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }


        // Get current location button
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.0.dp)
                .clip(CircleShape)
                .clickable {
                    scope.launch {
                        val location: Location? = uiState.getCurrentLocation()
                        if (location != null) {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(
                                        LatLng(location.latitude, location.longitude),
                                        15f
                                    )
                                ), 250
                            )
                            uiState.onEvent(
                                ChoiceLocationEvent.UpdateLocation(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    )
                                )
                            )
                        }
                    }
                }
                .background(Color.White)
                .size(45.dp)
                .padding(10.dp)
                .wrapContentSize(Alignment.Center),

            ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.baseline_my_location_24),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color(0xFF396E38))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.0.dp)
                .clip(RoundedCornerShape(13.dp))
                .clickable {
                    if (state.value.address != null) {
                        Di.sharedData.value.setData("payment-method", PaymentData(amount, state.value.address))
                        navController().navigate(Views.PaymentMethodView.route)
                    }
                }
                .background(Scheme.primary)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .wrapContentSize(Alignment.Center),

            ) {
            Text(
                text = "Confirm Location",
                style = TitleSmall.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun CenteredMarker(info: String? = null) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }
    var markerSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.ic_marker),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(35.dp)
                    .offset {
                        offset
                    }
                    .onGloballyPositioned {
                        offset = offset.copy(y = -(it.size.height / 2))
                        markerSize = it.size
                    },
                contentScale = ContentScale.FillBounds
            )

        if(info != null){
            Text(
                modifier = Modifier
                    .offset {
                        IntOffset(x = 0, y = -markerSize.height * 2)
                    }
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color.White.copy(.8f))
                    .padding(15.dp)
                    .wrapContentSize(Alignment.Center),
                text = info,
                style = TitleSmall.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}


data class PaymentData(
    val amount: Double,
    val address: Address?
)
