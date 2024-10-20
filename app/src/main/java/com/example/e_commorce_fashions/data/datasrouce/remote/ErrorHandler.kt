package com.example.e_commorce_fashions.data.datasrouce.remote

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.InternetConnectivityChecker
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.serialization.Serializable
import retrofit2.HttpException
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.util.logging.Level
import java.util.logging.Logger

// **
val LOGGER = Logger.getLogger("MyLogger")

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val response : Map<String, Any> = Json.parseToJsonElement(it.readUtf8()).jsonObject.toMap()
            return ErrorResponse(
                status = response["status"],
                message = response["message"] as String?,
                error = response["error"] as String?,
                details = response["details"] as String?
            )
//            Json.decodeFromJsonElement<ErrorResponse>(Json.parseToJsonElement(it.readUtf8()))
        }
    } catch (exception: Exception) {
        null
    }
}

//@Serializable
data class ErrorResponse(
    val status: Any? = null,
    val message: String? = null,
    val error: String? = null,
    val details: String? = null
)

suspend fun <T> errorHandlerBlock(
    checker: InternetConnectivityChecker,
    block: suspend () -> Either<Failure<Nothing>, T>
): Either<Failure<Nothing>, T> {
    return try {
        if (checker.hasConnection()) {
            block()
        } else {
            Either.Left(Failure.NoInternetConnection)
        }
    } catch (throwable: Throwable) {
        LOGGER.severe("Exception occurred: ${throwable.message}")
        LOGGER.log(Level.SEVERE, "Exception stack trace: ", throwable)
        Either.Left(throwable.getFailure)
    }
}
// **


val Throwable.getFailure: Failure<Nothing>
    get() = when (this) {
        is IOException -> Failure.NetworkError(message = this.message ?: "Network error occurred")
        is HttpException -> {
            val code = this.code()
            val errorResponse = convertErrorBody(this)

            val errorType = errorResponse?.let {
                ApiError.from(it.status ?: 0, it.error ?: "")
            }

            errorType?.let { Failure.CustomError(it) } ?: Failure.GenericError(code, errorResponse)

        }
        is FirebaseAuthException -> {
            val firebaseAuthError = ApiFirebaseError.from(this.errorCode)
            firebaseAuthError?.let { Failure.CustomFirebaseError(it) }
                ?: Failure.FirebaseError(this.message)
        }
        is FirebaseTooManyRequestsException -> {
            Failure.CustomFirebaseError(ApiFirebaseError.TOO_MANY_REQUESTS)
        }
        else -> {
            Failure.LocalError(this.message)
        }
    }


enum class ApiError(
    val status: Any,
    val message: String,
    val error: String
) {
    SERVER_ERROR(500, "Internal Server Error", "ServerError");

    companion object {
        fun from(status: Any, error: String): ApiError? {
            return entries.find {
                it.status == status && it.error.equals(error, ignoreCase = true)
            }
        }
    }
}

enum class ApiFirebaseError(
    val code: Any,
    val message: String,
    val error: String
) {
    INVALID_CREDENTIALS("ERROR_INVALID_CREDENTIAL", "The supplied auth credential is incorrect, malformed or has expired.", "InvalidCredentials"),
    USER_DISABLED("ERROR_USER_DISABLED", "This user account has been disabled.", "UserDisabled"),
    EMAIL_ALREADY_IN_USE("ERROR_EMAIL_ALREADY_IN_USE", "The email address is already in use by another account.", "EmailAlreadyInUse"),
    WEAK_PASSWORD("ERROR_WEAK_PASSWORD", "The password is too weak.", "WeakPassword"),
    USER_NOT_FOUND("ERROR_USER_NOT_FOUND", "User not found.", "UserNotFound"),
    OPERATION_NOT_ALLOWED("ERROR_OPERATION_NOT_ALLOWED", "Operation not allowed.", "OperationNotAllowed"),
    // TOO_MANY_REQUESTS is custom
    TOO_MANY_REQUESTS("ERROR_TOO_MANY_REQUESTS", "Too many requests. Try again later.", "TooManyRequests");

    companion object {
        fun from(code: Any): ApiFirebaseError? {
            return entries.find {
                if(code is String) it.code.toString().equals(code, ignoreCase = true)
                else it.code == code
            }
        }
    }
}
