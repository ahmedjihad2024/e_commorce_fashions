package com.example.e_commorce_fashions.presentation.views.profile.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleMedium
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileEvent
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileViewState
import com.example.e_commorce_fashions.presentation.views.search.view_state.SearchEvent
import kotlinx.coroutines.launch

data class SettingItem(val icon: ImageVector, val label: String, val onClick: () -> Unit)

@Composable
fun ProfileView(navController: () -> NavHostController, callbackUiState: () -> ProfileViewState) {

    val uiState = callbackUiState()
    val state by uiState.state.collectAsState()
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listenerState = rememberSaveable { mutableStateOf(false) }

    val settings1 = remember {
        mutableStateListOf(
            SettingItem(Icons.Rounded.Person, context.getString(R.string.personal_details)){
                navController().navigate(Views.PersonalDetailsView.route)
            },
            SettingItem(Icons.Rounded.ShoppingBag, context.getString(R.string.my_orders)){
                navController().navigate(Views.MyOrdersView.route)
            },
            SettingItem(Icons.Rounded.Favorite, context.getString(R.string.my_favourites)){
                navController().navigate(Views.FavoritesView.route)
            },
            SettingItem(Icons.Rounded.Settings, context.getString(R.string.settings)){},
        )
    }

    val settings2 = remember {
        mutableStateListOf(
            SettingItem(Icons.Rounded.Info, context.getString(R.string.faqs)){},
            SettingItem(Icons.Rounded.Shield, context.getString(R.string.privacy_policy)){},
            SettingItem(Icons.Rounded.Info, context.getString(R.string.about_us)){},
        )
    }

    LaunchedEffect(state.isLogout) {
        if (state.isLogout) {
            navController().navigate(Views.AuthWelcomeView.route) {
                popUpTo(Views.LayoutView.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(listenerState.value){
        if(!listenerState.value){
            val listener = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        navController().currentBackStackEntry!!.lifecycle.removeObserver(this)
                    }else if(
                        event == Lifecycle.Event.ON_RESUME
                    ){
                        uiState.onEvent(
                            ProfileEvent.GetUserDetails
                        )
                    }
                }
            }
            navController().currentBackStackEntry!!.lifecycle.addObserver(listener)
        }
    }

    Scaffold(
        containerColor = Scheme.primary
    ) { scaffoldPadding ->
        SetState(
            requestState = state.requestState,
            onSuccess = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(scaffoldPadding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .shadow(
                                elevation = 25.dp,
                                shape = RoundedCornerShape(15.dp),
                                spotColor = Scheme.onPrimary.copy(0.1f)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .fillMaxWidth()
                            .background(Scheme.primary)
                            .padding(7.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .size(60.dp),
                            painter = rememberAsyncImagePainter(
                                state.userDetails!!.pictureUrl,
                                contentScale = ContentScale.FillBounds
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = "profile-image"
                        )

                        Column(
                        ) {
                            Text(
                                state.userDetails!!.name!!,
                                style = TitleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Scheme.onPrimary,
                                    fontSize = 14.sp
                                )
                            )
                            if (state.userDetails!!.email != null) {
                                Text(
                                    state.userDetails!!.email!!,
                                    style = TitleSmall.copy(
                                        fontWeight = FontWeight.Normal,
                                        color = Scheme.onPrimary.copy(.5f),
                                        lineHeight = 13.sp,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // logout button
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .clickable {
                                showDialog.value = true
                            }
                            .background(
                                Scheme.onPrimary,
                            )
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.Logout,
                            null,
                            tint = Scheme.primary,
                            modifier = Modifier
                                .size(23.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            stringResource(R.string.logout),
                            style = TitleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Scheme.primary,
                                fontSize = 14.sp
                            )
                        )
                    }

                    // settings
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Scheme.onPrimary.copy(.05f),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(vertical = 15.dp)
                    ) {
                        Column {
                            for (item in settings1) {
                                ProfileItemCard(item.icon, item.label, item.onClick)
                            }
                        }
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = Scheme.onPrimary.copy(.05f),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(vertical = 15.dp)
                    ) {
                        Column {
                            for (item in settings2) {
                                ProfileItemCard(item.icon, item.label, item.onClick)
                            }
                        }
                    }
                }
            },
            onError = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        state.errorMessage,
                        style = TitleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Scheme.onPrimary,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                uiState.onEvent(ProfileEvent.GetUserDetails)
                            }
                            .background(Scheme.primary)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            stringResource(R.string.try_again),
                            style = TitleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Scheme.onPrimary,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            },
            onLoading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = Scheme.onPrimary,
                        trackColor = Scheme.primary,
                        strokeWidth = 1.5.dp
                    )
                }
            }
        )

        if (showDialog.value) {
            LogoutConfirmationDialog(
                showDialog = showDialog.value,
                onDismiss = { showDialog.value = false },
                onConfirm = {
                    scope.launch {
                        showDialog.value = false
                        uiState.onEvent(ProfileEvent.Logout)
                    }
                }
            )
        }
    }
}


@Composable
fun ProfileItemCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = 15.dp,
                vertical = 10.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = Scheme.onPrimary,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Scheme.onPrimary.copy(.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            )
            Text(
                label,
                style = TitleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Scheme.onPrimary,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = stringResource(R.string.logout), style = TitleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Scheme.onPrimary
                    )
                )
            },
            text = {
                Text(
                    stringResource(R.string.are_you_sure), style = TitleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Scheme.onPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Scheme.primary
                    )
                ) {
                    Text(
                        stringResource(R.string.logout), style = TitleLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Scheme.onPrimary
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text(
                        stringResource(R.string.cancel), style = TitleLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Scheme.onPrimary
                        )
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Scheme.primary,
        )
    }
}
