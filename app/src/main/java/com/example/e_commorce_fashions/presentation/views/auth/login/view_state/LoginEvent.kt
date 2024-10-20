package com.example.e_commorce_fashions.presentation.views.auth.login.view_state

import android.content.Intent
import com.facebook.login.LoginResult

sealed class LoginEvent{
    data class Login(val email: String, val password: String): LoginEvent()
    data class LoginWithGoogle(val intent: Intent): LoginEvent()
    data class LoginWithFacebook(val loginResult: LoginResult): LoginEvent()
}