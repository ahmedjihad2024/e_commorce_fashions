package com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state

sealed class SignUpEvent{
    data class SignUp(val name: String, val email: String, val password: String): SignUpEvent()
}