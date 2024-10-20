package com.example.e_commorce_fashions.presentation.views.auth.sign_up.view_state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UserDetailsReq
import com.example.e_commorce_fashions.data.requests.UserRegisterReq
import com.example.e_commorce_fashions.domain.models.UserRegisterResult
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.UpdateUserDetailsUseCase
import com.example.e_commorce_fashions.domain.use_case.UserRegisterResultUseCase
import com.example.e_commorce_fashions.presentation.views.view_all.view_state.ViewAllProductsStateView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignUpViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewState::class.java)) {
            return SignUpViewState(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SignUpViewState(private val repository: Repository): ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state get() = _state.asStateFlow()

    private val _userRegisterUseCase = UserRegisterResultUseCase(repository)
    private val _updateUserDetailsUseCase = UpdateUserDetailsUseCase(repository)

    fun onEvent(event: SignUpEvent){
        when(event){
            is SignUpEvent.SignUp -> {
                _userRegister(event.name, event.email, event.password)
            }
        }
    }

    private fun _userRegister(
        name: String,
        email: String,
        password: String
    ){
        viewModelScope.launch {

            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }

            val result: Either<Failure<Nothing>, UserRegisterResult> = _userRegisterUseCase.execute(UserRegisterReq(name, email, password))

            result.fold(
                right = {
                    updateUserDetails(
                        UserDetailsReq(
                            name = name,
                        )
                    )
                },
                left = { left: Failure<Nothing> ->
                    _state.update {
                        it.copy(
                            requestState = RequestState.ERROR(),
                            errorMessage = left.userMessage
                        )
                    }
                }
            )

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