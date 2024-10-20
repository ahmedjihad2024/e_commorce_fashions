package com.example.e_commorce_fashions.data.datasrouce.remote



sealed class Failure<out T> {
    // error from the server and i customized it
    data class CustomError<T>(val message: ApiError? = null) : Failure<T>()
    // error from the server directly
    data class GenericError<T>(val code: Int? = null, val error: ErrorResponse? = null): Failure<T>()
    // error that may happened from my code
    data class LocalError<T>( val message: String? = null): Failure<T>()
    // error that may happened from the internet
    data class NetworkError<T>(val message: String? = null) : Failure<T>()
    // error that may happened from firebase and i customized it
    data class CustomFirebaseError<T>(val message: ApiFirebaseError? = null) : Failure<T>()
    // error that may happened from firebase directly
    data class FirebaseError<T>(val message: String? = null) : Failure<T>()
    // there is no internet connection
    data object NoInternetConnection: Failure<Nothing>()
}