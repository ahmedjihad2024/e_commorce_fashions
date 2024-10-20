package com.example.e_commorce_fashions.presentation.views.auth.login.view_state

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.AuthType
import com.example.e_commorce_fashions.data.requests.UserDetailsReq
import com.example.e_commorce_fashions.data.requests.UserLoginAuthTokenReq
import com.example.e_commorce_fashions.data.requests.UserLoginReq
import com.example.e_commorce_fashions.data.requests.UserRegisterReq
import com.example.e_commorce_fashions.domain.models.UserLoginResult
import com.example.e_commorce_fashions.domain.models.UserRegisterResult
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.LoginUserUseCase
import com.example.e_commorce_fashions.domain.use_case.LoginUserWithFacebookUseCase
import com.example.e_commorce_fashions.domain.use_case.LoginUserWithGoogleUseCase
import com.example.e_commorce_fashions.domain.use_case.UpdateUserDetailsUseCase
import com.example.e_commorce_fashions.presentation.common.GoogleAuth.GoogleAuthUiClient
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewStateFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewState::class.java)) {
            return LoginViewState(repository, Di.googleAuthUiClient.value) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewState(private val repository: Repository, val googleAuth: GoogleAuthUiClient) : ViewModel() {

    init {
        Log.d("my-data", "init")
    }

    private val _state = MutableStateFlow(UiState())
    val state get() = _state.asStateFlow()

    private val _loginUseCase = LoginUserUseCase(repository)
    private val _loginWithGoogleUseCase = LoginUserWithGoogleUseCase(repository)
    private val _loginWithFacebookUseCase = LoginUserWithFacebookUseCase(repository)
    private val _updateUserDetailsUseCase = UpdateUserDetailsUseCase(repository)

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> _loginUser(event.email, event.password)
            is LoginEvent.LoginWithGoogle -> _loginWithGoogle(event.intent)
            is LoginEvent.LoginWithFacebook -> _loginWithFacebook(event.loginResult)
        }
    }

    private fun _loginUser(email: String, password: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(requestState = RequestState.LOADING)
            }

            val result: Either<Failure<Nothing>, UserLoginResult> = _loginUseCase.execute(
                UserLoginReq(email, password)
            )

            result.fold(
                right = { data ->
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.SUCCESS
                        )
                    }

                    Log.d("my-data", data.uid)
                },
                left = { left: Failure<Nothing> ->
                    Log.d("my-data", left.toString())
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                }
            )
        }
    }

    private fun _loginWithGoogle(intent: Intent) {
        val credential = googleAuth.signInWithIntent(intent)
        val authToken = UserLoginAuthTokenReq(credential.googleIdToken!!, AuthType.GOOGLE)
        viewModelScope.launch {
            _state.update {
                it.copy(requestState = RequestState.LOADING)
            }

            val result = _loginWithGoogleUseCase.execute(authToken)
            result.fold(
                right = { data ->

                    var email : String? = null
                    var pictureUrl : String? = null

                    try{
                        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                        email = account?.email
                        pictureUrl = account?.photoUrl?.path
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                    updateUserDetails(
                        UserDetailsReq(
                            name = credential.displayName,
                            pictureUrl = credential.profilePictureUri?.path ?: pictureUrl,
                            email = email,
                        )
                    )
                },
                left = { left: Failure<Nothing> ->
                    Log.d("my-data", left.toString())
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                }
            )
        }
    }

    private fun _loginWithFacebook(loginResult: LoginResult) {
        val authToken = UserLoginAuthTokenReq(loginResult.accessToken.token, AuthType.FACEBOOK)
        viewModelScope.launch {
            _state.update {
                it.copy(requestState = RequestState.LOADING)
            }


            val result = _loginWithFacebookUseCase.execute(authToken)
            result.fold(
                right = { data ->
                    getFacebookUserInfo(loginResult.accessToken) { name, url ->
                        updateUserDetails(
                            UserDetailsReq(
                                name = name,
                                pictureUrl = url,
                            )
                        )
                    }

                    Log.d("my-data", data.uid)
                },
                left = { left: Failure<Nothing> ->
                    Log.d("my-data", left.toString())
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                }
            )
        }
    }

    private fun getFacebookUserInfo(
        accessToken: AccessToken,
        onUserInfoReceived: (name: String?, url: String?) -> Unit
    ) {
        viewModelScope.launch {
            val request = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
                try {
                    // Parse the JSON object to get user details
                    val name = jsonObject?.getString("name")
                    val pictureUrl = jsonObject
                        ?.getJSONObject("picture")
                        ?.getJSONObject("data")
                        ?.getString("url")

                    onUserInfoReceived(name, pictureUrl)

                } catch (e: Exception) {
                    e.printStackTrace()
                    onUserInfoReceived(null, null);
                }
            }

            val parameters = Bundle()
            parameters.putString("fields", "id,name,picture.type(large)")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    private fun updateUserDetails(userDetails: UserDetailsReq) {
        viewModelScope.launch {
            _updateUserDetailsUseCase.execute(
                userDetails
            ).fold(
                right = {
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.SUCCESS
                        )
                    }
                },
                left = { left ->
                    _state.update { oldState ->
                        oldState.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                }
            )
        }
    }
}