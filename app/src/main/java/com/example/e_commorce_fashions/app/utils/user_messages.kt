package com.example.e_commorce_fashions.app.utils

import com.example.e_commorce_fashions.data.datasrouce.remote.ApiError
import com.example.e_commorce_fashions.data.datasrouce.remote.ApiFirebaseError
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure

val Failure<Nothing>.userMessage: String
    get() = when (this) {
        is Failure.LocalError -> this.message ?: "An unexpected error occurred. Please try restarting the app."
        is Failure.NetworkError -> "Unable to connect to the server. Please check your internet connection and try again."
        Failure.NoInternetConnection -> "No internet connection detected. Please connect to the internet and try again."
        is Failure.GenericError -> this.error?.message ?: DEFAULT_ERROR_MESSAGE
        is Failure.CustomError -> when (this.message) {
            ApiError.SERVER_ERROR -> DEFAULT_ERROR_MESSAGE
            else -> DEFAULT_ERROR_MESSAGE
        }
        // i can customize each message for each error
        is Failure.CustomFirebaseError -> when (this.message) {
            ApiFirebaseError.TOO_MANY_REQUESTS -> "Too many requests. Try again later."
            ApiFirebaseError.INVALID_CREDENTIALS -> "Email or password is incorrect"
            else -> this.message?.message!!
        }
        is Failure.FirebaseError -> this.message!!
    }

const val DEFAULT_ERROR_MESSAGE = "Oops! Something went wrong. Please try again."