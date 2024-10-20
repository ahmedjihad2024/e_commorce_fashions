package com.example.e_commorce_fashions.presentation.views.profile.view_state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.GetUserDetailsUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewStateFactory(private val repository: Repository = Di.repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewState::class.java)) {
            return ProfileViewState(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewState(repository: Repository) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()


    private val _getUserDetails = GetUserDetailsUseCase(repository)

    init {
        _getUserDetails()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.GetUserDetails -> {
                _getUserDetails()
            }

            is ProfileEvent.Logout -> {
                _logout()
            }

            else -> {}
        }
    }

    private fun _getUserDetails() {
        viewModelScope.launch {

//            _state.update {
//                it.copy(
//                    requestState = RequestState.LOADING
//                )
//            }

            val result = _getUserDetails.execute(Unit)

            result.fold(
                left = { left ->
//                    _state.update {
//                        it.copy(
//                            errorMessage = left.userMessage,
//                            requestState = RequestState.ERROR()
//                        )
//                    }
                },
                right = { right ->

                    _state.update {
                        it.copy(
                            userDetails = right.copy(
                                email = if (right.email != null && right.email.trim()
                                        .isNotEmpty()
                                ) right.email else FirebaseAuth.getInstance().currentUser!!.email,
                                name = if (right.name != null && right.name.trim()
                                        .isNotEmpty()
                                ) right.name else FirebaseAuth.getInstance().currentUser!!.displayName,
                                pictureUrl = if (right.pictureUrl != null && right.pictureUrl.trim()
                                        .isNotEmpty()
                                ) right.pictureUrl else FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
                            ),
                            requestState = RequestState.SUCCESS
                        )
                    }

                }
            )

        }
    }

    private fun _logout() {
        viewModelScope.launch {
            try{
                Di.preferences.value.clearUserToken()
                FirebaseAuth.getInstance().signOut()
                _state.update {
                    it.copy(
                        isLogout = true
                    )
                }
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

}