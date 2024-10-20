package com.example.e_commorce_fashions.presentation.views.personal_details.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.presentation.common.SetState
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.personal_details.view_state.PersonalDetailsEvent
import com.example.e_commorce_fashions.presentation.views.personal_details.view_state.PersonalDetailsViewState
import com.example.e_commorce_fashions.presentation.views.personal_details.view_state.PersonalDetailsViewStateFactory
import com.example.e_commorce_fashions.presentation.views.profile.view.ProfileItemCard
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileEvent
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileViewState
import com.example.e_commorce_fashions.presentation.views.profile.view_state.ProfileViewStateFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PersonalDetailsView(
    navController: () -> NavHostController,
) {

    val context = LocalContext.current
    val uiState = viewModel<PersonalDetailsViewState>(factory = PersonalDetailsViewStateFactory())
    val snackBar = SnackbarHostState()

    val state by uiState.state.collectAsStateWithLifecycle()

    val scheme = Scheme
    val userName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val selectedImage = remember { mutableStateOf<Uri?>(null) }

    val onClickBack: State<() -> Unit> = rememberUpdatedState {
        run {
            navController().navigateUp()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri: Uri? ->
        if(uri != null) selectedImage.value = uri
    }

    val isUpdateActive = remember(userName, email, selectedImage) {
        derivedStateOf {
            userName.value != state.userDetails?.name || email.value != state.userDetails?.email || selectedImage.value != null
        }
    }

    LaunchedEffect(false){
        uiState.snackBarState.collect {
            snackBar.showSnackbar(it)
        }
    }

    LaunchedEffect(state.userDetails) {
        if (state.userDetails != null) {
            if (state.userDetails!!.name !== null && state.userDetails!!.name!!.isNotEmpty()) userName.value =
                state.userDetails!!.name!!
            if (state.userDetails!!.email !== null && state.userDetails!!.email!!.isNotEmpty()) email.value =
                state.userDetails!!.email!!
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Scheme.primary,
        snackbarHost = {
            SnackbarHost(snackBar)
        }
    ) { scaffoldPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onClickBack.value()
                            }
                            .background(Scheme.onPrimary)
                            .size(50.dp)
                            .padding(15.dp)
                            .wrapContentSize(Alignment.Center),
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                R.drawable.ic_arrow_back,
                                contentScale = ContentScale.Crop
                            ),
                            contentDescription = "menu",
                            colorFilter = ColorFilter.tint(Scheme.primary)
                        )
                    }
                }

                SetState(
                    requestState = state.requestState,
                    onSuccess = {
                        Column(
                        ) {
                            Spacer(
                                modifier = Modifier.height(40.dp)
                            )

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box {
                                        Image(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(18.dp))
                                                .size(110.dp)
                                                .background(
                                                    Scheme.onPrimary.copy(.05f),
                                                ),
                                            painter = rememberAsyncImagePainter(
                                                selectedImage.value
                                                    ?: state.userDetails!!.pictureUrl,
                                                contentScale = ContentScale.FillBounds
                                            ),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = "profile-image"
                                        )
                                        Box(
                                            Modifier
                                                .align(Alignment.BottomEnd)
                                                .offset {
                                                    IntOffset(20, 20)
                                                }
                                                .shadow(
                                                    10.dp,
                                                    RoundedCornerShape(10.dp),
                                                    spotColor = Scheme.onPrimary.copy(.05f)
                                                )
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    galleryLauncher.launch("image/*")
                                                }
                                                .background(Scheme.primary)
                                                .size(30.dp)
                                                .padding(6.dp)
                                                .wrapContentSize(Alignment.Center)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                null,
                                                tint = Scheme.onPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        stringResource(R.string.upload_image),
                                        style = TitleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Scheme.onPrimary,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(35.dp)
                            )

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    stringResource(R.string.user_name),
                                    style = TitleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Scheme.onPrimary.copy(.5f),
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.weight(.35f)
                                )

                                BasicTextField(
                                    modifier = Modifier
                                        .weight(.65f)
                                        .drawBehind {
                                            val borderWidth = 1.dp.toPx()
                                            drawLine(
                                                color = scheme.onPrimary.copy(.3f),
                                                start = Offset(0f, this.size.height),
                                                end = Offset(this.size.width, this.size.height),
                                                strokeWidth = borderWidth
                                            )
                                        }
                                        .padding(start = 10.dp)
                                        .padding(vertical = 3.dp),
                                    value = userName.value,
                                    onValueChange = { userName.value = it },
                                    singleLine = true,
                                    textStyle = TitleLarge.copy(
                                        color = Scheme.onPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                    ),
                                    decorationBox = { innerTextField ->
                                        innerTextField()
                                        if (userName.value.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.user_name),
                                                style = TitleLarge.copy(
                                                    color = Scheme.onPrimary.copy(.4f),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                )
                                            )
                                        }
                                    }
                                )
                            }

//                            Spacer(
//                                modifier = Modifier.height(20.dp)
//                            )
//
//                            Row(
//                                Modifier.fillMaxWidth(),
//                                verticalAlignment = Alignment.Bottom
//                            ) {
//                                Text(
//                                    stringResource(R.string.email),
//                                    style = TitleSmall.copy(
//                                        fontWeight = FontWeight.Bold,
//                                        color = Scheme.onPrimary.copy(.5f),
//                                        fontSize = 14.sp
//                                    ),
//                                    modifier = Modifier.weight(.35f)
//                                )
//
//                                BasicTextField(
//                                    modifier = Modifier
//                                        .weight(.65f)
//                                        .drawBehind {
//                                            val borderWidth = 1.dp.toPx()
//                                            drawLine(
//                                                color = scheme.onPrimary.copy(.3f),
//                                                start = Offset(0f, this.size.height),
//                                                end = Offset(this.size.width, this.size.height),
//                                                strokeWidth = borderWidth
//                                            )
//                                        }
//                                        .padding(start = 10.dp)
//                                        .padding(vertical = 3.dp),
//                                    value = email.value,
//                                    onValueChange = { email.value = it },
//                                    singleLine = true,
//                                    textStyle = TitleLarge.copy(
//                                        color = Scheme.onPrimary,
//                                        fontWeight = FontWeight.Medium,
//                                        fontSize = 14.sp,
//                                    ),
//                                    decorationBox = { innerTextField ->
//                                        innerTextField()
//                                        if (email.value.isEmpty()) {
//                                            Text(
//                                                text = stringResource(R.string.email),
//                                                style = TitleLarge.copy(
//                                                    color = Scheme.onPrimary.copy(.4f),
//                                                    fontWeight = FontWeight.Medium,
//                                                    fontSize = 14.sp,
//                                                )
//                                            )
//                                        }
//                                    }
//                                )
//                            }

                            Spacer(
                                modifier = Modifier.height(30.dp)
                            )

                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(13.dp))
                                    .clickable {
                                        if (isUpdateActive.value) {
                                            uiState.onEvent(
                                                PersonalDetailsEvent.UpdateProfileDetails(
                                                    if (userName.value != state.userDetails?.name) userName.value else null,
                                                    if (email.value != state.userDetails?.email) email.value else null,
                                                    selectedImage.value
                                                )
                                            )
                                        }
                                    }
                                    .background(
                                        if (isUpdateActive.value) Scheme.onPrimary else Scheme.onPrimary.copy(
                                            .5f
                                        ),
                                    )
                                    .padding(vertical = 15.dp)
                                    .wrapContentSize(Alignment.Center),
                            ) {
                                Text(
                                    stringResource(R.string.update),
                                    style = TitleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Scheme.primary,
                                        fontSize = 14.sp
                                    )
                                )
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
                                        uiState.onEvent(PersonalDetailsEvent.GetUserDetails)
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
            }

            AnimatedVisibility(
                visible = state.updatingState == RequestState.LOADING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .wrapContentSize(Alignment.Center).clickable(
                            enabled = false,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){}
                ) {
                    CircularProgressIndicator(
                        color = Scheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
    }
}