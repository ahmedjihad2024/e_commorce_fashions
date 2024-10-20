package com.example.e_commorce_fashions.presentation.views.auth.login.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.presentation.common.AppLogo
import com.example.e_commorce_fashions.presentation.common.GoogleAuth.GoogleAuthUiClient
import com.example.e_commorce_fashions.presentation.common.SocialMediaButton
import com.example.e_commorce_fashions.presentation.common.TextFormField
import com.example.e_commorce_fashions.presentation.common.sh
import com.example.e_commorce_fashions.presentation.common.statusBarHeight
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.auth.login.view_state.LoginEvent
import com.example.e_commorce_fashions.presentation.views.auth.login.view_state.LoginViewState
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpViewState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.identity.Identity

//val uiState: LoginViewState = viewModel<LoginViewState>(
//    key = "login-view-state",
//    factory = object : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(
//            modelClass: Class<T>,
//            extras: CreationExtras
//        ): T {
//            return LoginViewState(
//                googleAuth = GoogleAuthUiClient(
//                    context,
//                    Identity.getSignInClient(context)
//                )
//            ) as T
//        }
//    },
//    viewModelStoreOwner = navController.currentBackStackEntry!!
//)


@Composable
fun LoginView(navController: NavHostController, uiState: LoginViewState) {

    val state = uiState.state.collectAsState()

    val focusManager = LocalFocusManager.current
    val snackBar = SnackbarHostState()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val passwordRequester = remember { FocusRequester() }
    val emailRequester = remember { FocusRequester() }


    LaunchedEffect(key1 = state.value.requestState) {
        if (state.value.requestState == RequestState.SUCCESS) {
            // TODO: navigate to layout
            navController.navigate(Views.LayoutView.route) {
                popUpTo(Views.AuthWelcomeView.route) {
                    inclusive = true
                }
            }
        } else if (state.value.requestState == RequestState.ERROR()) {
            snackBar.showSnackbar(state.value.errorMessage, withDismissAction = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBar) }
    ) { scaffoldPadding ->
        Box {
            Column(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .background(Scheme.primary)
                    .fillMaxSize()
                    .verticalScroll(ScrollState(0))
                    .padding(20.dp)
                    .padding(scaffoldPadding)
            ) {
                Box(
                    modifier = Modifier
                        .height(.25.sh)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                ) {
                    AppLogo()
                }
                Text(
                    text = stringResource(R.string.welcome),
                    style = TitleLarge.copy(
                        color = Scheme.onPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = stringResource(R.string.please_login),
                    style = TitleLarge.copy(
                        color = Scheme.onPrimary.copy(.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))
                TextFormField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    focusRequester = emailRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_email),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary.copy(.4f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.email),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextFormField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    focusRequester = passwordRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_password),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary.copy(.4f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.password),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                )
                Spacer(modifier = Modifier.height(45.dp))
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Scheme.onPrimary)
                        .fillMaxWidth()
                        .height(60.dp)
                        .wrapContentSize(Alignment.Center),
                    color = Color.Transparent,
                    onClick = {
                        if (email.value.isEmpty()) {
                            emailRequester.requestFocus()
                        } else if (password.value.isEmpty()) {
                            passwordRequester.requestFocus()
                        } else {
                            focusManager.clearFocus()
                            uiState.onEvent(LoginEvent.Login(email.value, password.value))
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        text = stringResource(R.string.login),
                        style = TitleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Scheme.primary,
                            fontSize = 16.sp,
                        )
                    )
                }

                // Or
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val modifier =
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Scheme.onPrimary.copy(.1f))
                    Spacer(modifier)
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = stringResource(R.string.or),
                        style = TitleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Scheme.onPrimary,
                            fontSize = 12.sp,
                        )
                    )
                    Spacer(modifier)
                }

                // facebook button
                FacebookAuthButton(uiState::onEvent)

                Spacer(modifier = Modifier.height(15.dp))
                // google button
                GoogleAuthButton(uiState::onEvent, uiState.googleAuth::getIntentSender)


            }

            // Loading
            AnimatedVisibility(
                visible = state.value.requestState == RequestState.LOADING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = Scheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun GoogleAuthButton(onEvent: (LoginEvent) -> Unit, getIntentSender: suspend () -> IntentSender?) {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { launch ->
            if (launch.resultCode == RESULT_OK) {
                onEvent(
                    LoginEvent.LoginWithGoogle(
                        launch.data ?: return@rememberLauncherForActivityResult
                    )
                )
            }
        }
    )

    SocialMediaButton(
        modifier = Modifier.border(1.dp, Scheme.onPrimary.copy(.2f), CircleShape),
        backgroundColor = Color.Transparent,
        painter = rememberAsyncImagePainter(
            R.drawable.ic_google,
            contentScale = ContentScale.Fit
        ),
        iconColor = Scheme.onPrimary,
        annotatedString = buildAnnotatedString {
            append(stringResource(R.string.continue_with))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.google))
            }
        },
        textColor = Scheme.onPrimary.copy(.5f),
        onClick = {
            scope.launch {
                val intentSender = getIntentSender() ?: return@launch
                val intentSenderRequest =
                    IntentSenderRequest.Builder(intentSender).build()
                launcher.launch(intentSenderRequest)
            }
        }
    )
}

@Composable
fun FacebookAuthButton(onEvent: (LoginEvent) -> Unit) {
    val context = LocalContext.current
    val loginManager = LoginManager.getInstance()
    val callbackManager = remember { CallbackManager.Factory.create() }
    val launcher = rememberLauncherForActivityResult(
        loginManager.createLogInActivityResultContract(callbackManager, null)
    ) {
    }

    DisposableEffect(Unit) {

//        loginManager.logInWithReadPermissions(context as Activity, listOf("public_profile", "email"))

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                // do nothing
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                onEvent(LoginEvent.LoginWithFacebook(result))
            }
        })

        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }

    SocialMediaButton(
        backgroundColor = Color(0xFF4285F4),
        icon = Icons.Rounded.Facebook,
        iconColor = Color.White,
        annotatedString = buildAnnotatedString {
            append(stringResource(R.string.continue_with))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.facebook))
            }
        },
        textColor = Color.White,
        onClick = {
            launcher.launch(listOf("public_profile", "email"))
        }
    )
}


