
package com.example.e_commorce_fashions.presentation.views.auth.sign_up.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.e_commorce_fashions.R
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.presentation.common.AppLogo
import com.example.e_commorce_fashions.presentation.common.TextFormField
import com.example.e_commorce_fashions.presentation.common.sh
import com.example.e_commorce_fashions.presentation.resources.config.navigator.Views
import com.example.e_commorce_fashions.presentation.resources.config.theme.Scheme
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleLarge
import com.example.e_commorce_fashions.presentation.resources.config.theme.TitleSmall
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpEvent
import com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state.SignUpViewState
import kotlinx.coroutines.launch

@Composable
fun SignUpView(navController: NavHostController, uiState: SignUpViewState) {
    val state = uiState.state.collectAsState()

    val scope = rememberCoroutineScope()
    val snackBar = SnackbarHostState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var userName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val byAgree = rememberSaveable { mutableStateOf(false) }
    val scrollState = remember { ScrollState(0) }
    val notSame = rememberSaveable { mutableStateOf(false) }

    val nameRequester = remember { FocusRequester() }
    val emailRequester = remember { FocusRequester() }
    val passwordRequester = remember { FocusRequester() }
    val confirmPasswordRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = state.value.requestState){
        if(state.value.requestState == RequestState.SUCCESS){
            navController.navigate(Views.SuccessRegisterView.route){
                popUpTo(Views.AuthWelcomeView.route){
                    inclusive = true
                }
            }
        }else if(state.value.requestState == RequestState.ERROR()){
            snackBar.showSnackbar(state.value.errorMessage, withDismissAction = true)
        }
    }

    Scaffold (
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
                    .verticalScroll(scrollState)
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
                    text = stringResource(R.string.sign_up),
                    style = TitleLarge.copy(
                        color = Scheme.onPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = stringResource(R.string.create_an_new_account),
                    style = TitleLarge.copy(
                        color = Scheme.onPrimary.copy(.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))
                TextFormField(
                    value = userName,
                    onValueChange = { userName = it },
                    focusRequester = nameRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.user_name),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary.copy(.4f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.user_name),
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
                    value = email,
                    onValueChange = { email = it },
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
                    value = password,
                    onValueChange = { password = it },
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
                Spacer(modifier = Modifier.height(15.dp))
                TextFormField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (password != confirmPassword) {
                            notSame.value = true
                        } else {
                            notSame.value = false
                        }
                    },
                    focusRequester = confirmPasswordRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true,
                    isError = notSame.value,
                    supportingText = {
                        if (notSame.value) {
                            Text(
                                text = stringResource(R.string.password_not_same),
                                style = TitleLarge.copy(
                                    color = Color.Red,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                )
                            )
                        }
                    },
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
                            text = stringResource(R.string.confirm_password),
                            style = TitleLarge.copy(
                                color = Scheme.onPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            )
                        )
                    },
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Checkbox(
                        checked = byAgree.value,
                        onCheckedChange = {
                            byAgree.value = it
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Scheme.onPrimary,
                            uncheckedColor = Scheme.onPrimary.copy(.5f),
                        )
                    )
                    Text(
                        text = stringResource(R.string.by_creating_an_account),
                        softWrap = true,
                        style = TitleLarge.copy(
                            color = if (byAgree.value) Scheme.onPrimary else Scheme.onPrimary.copy(
                                .5f
                            ),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(35.dp))
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Scheme.onPrimary)
                        .fillMaxWidth()
                        .height(60.dp)
                        .wrapContentSize(Alignment.Center),
                    color = Color.Transparent,
                    onClick = {

                        if (userName.isEmpty()) {
                            nameRequester.requestFocus()
                        } else if (email.isEmpty()) {
                            emailRequester.requestFocus()
                        } else if (password.isEmpty()) {
                            passwordRequester.requestFocus()
                        } else if (confirmPassword.isEmpty()) {
                            confirmPasswordRequester.requestFocus()
                        } else if (!byAgree.value) {
                            scope.launch {
                                snackBar.showSnackbar(
                                    context.getString(R.string.please_agree),
                                    withDismissAction = true
                                )
                            }
                        } else {
                            if(password == confirmPassword) {
                                focusManager.clearFocus()
                                uiState.onEvent(SignUpEvent.SignUp(userName, email, password))
                            }
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