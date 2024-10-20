package com.example.e_commorce_fashions.presentation.views.personal_details.view_state

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.RequestState
import com.example.e_commorce_fashions.app.utils.userMessage
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.data.requests.UriReq
import com.example.e_commorce_fashions.data.requests.UserDetailsReq
import com.example.e_commorce_fashions.domain.repository.Repository
import com.example.e_commorce_fashions.domain.use_case.GetUserDetailsUseCase
import com.example.e_commorce_fashions.domain.use_case.UpdateNameAndEmailUseCase
import com.example.e_commorce_fashions.domain.use_case.UpdateProfilePicture
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PersonalDetailsViewStateFactory(val repository: Repository = Di.repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PersonalDetailsViewState(repository) as T
    }
}

class PersonalDetailsViewState(repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(PersonalDetailsState())
    private val _snackBarState = MutableSharedFlow<String>()
    val state = _state.asStateFlow()
    val snackBarState = _snackBarState.asSharedFlow()

    private val _getUserDetails = GetUserDetailsUseCase(repository)
    private val _updateNameAndEmail = UpdateNameAndEmailUseCase(repository)
    private val _updateProfilePicture = UpdateProfilePicture(repository)

    init {
        _getUserDetails()
    }

    fun onEvent(
        event: PersonalDetailsEvent
    ) {
        when (event) {
            is PersonalDetailsEvent.GetUserDetails -> {
                _getUserDetails()
            }

            is PersonalDetailsEvent.UpdateProfileDetails -> {
                _updateNameAndEmail(
                    event.name,
                    event.email,
                    event.pictureUrl
                )
            }
        }
    }

    private fun _getUserDetails() {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    requestState = RequestState.LOADING
                )
            }

            val result = _getUserDetails.execute(Unit)

            result.fold(
                left = { left ->
                    _state.update {
                        it.copy(
                            errorMessage = left.userMessage,
                            requestState = RequestState.ERROR()
                        )
                    }
                },
                right = { right ->

                    _state.update {
                        it.copy(
                            userDetails = right.copy(
                                email = FirebaseAuth.getInstance().currentUser!!.email,
                                name = FirebaseAuth.getInstance().currentUser!!.displayName,
                                pictureUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
                            ),
                            requestState = RequestState.SUCCESS
                        )
                    }

                }
            )

        }
    }

    private fun _updateNameAndEmail(
        name: String?,
        email: String?,
        pictureUrl: Uri?,
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    updatingState = RequestState.LOADING
                )
            }

            if (pictureUrl != null) {
                val result = _updateProfilePicture.execute(
                    UriReq(pictureUrl)
                )
                result.fold(
                    left = { left ->
                        _state.update {
                            it.copy(
                                updatingState = RequestState.ERROR()
                            )
                        }
                        launch {
                            _snackBarState.emit(left.userMessage)
                        }
                    },
                    right = { right ->

                    }
                )
            }

            if (name != null || email != null) {
                val result2 = _updateNameAndEmail.execute(
                    UserDetailsReq(
                        name = name,
                        email = email
                    )
                )
                result2.fold(
                    left = { left ->
                        _state.update {
                            it.copy(
                                updatingState = RequestState.ERROR()
                            )
                        }
                        launch {
                            _snackBarState.emit(left.userMessage)
                        }
                    },
                    right = { right ->

                    }
                )
            }

            if (_state.value.updatingState !is RequestState.ERROR) {
                _state.update {
                    it.copy(
                        updatingState = RequestState.SUCCESS
                    )
                }
                _snackBarState.emit("Successfully Updated")
            }

        }
    }
}