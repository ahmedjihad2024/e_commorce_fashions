package com.example.e_commorce_fashions.data.responses

import com.google.firebase.firestore.DocumentReference
import com.google.gson.annotations.SerializedName


data class UserRegisterResultResp(
    val uid: String?,
)

data class UserLoginResultResp(
    val uid: String?,
)

data class CustomerIdResponse(
    @SerializedName("id")
    val id: String?
)

data class EphemeralKeyResponse(
    @SerializedName("secret")
    val key: String?
)

data class ClientSecretResponse(
    @SerializedName("client_secret")
    val client_secret: String?
)

data class AccessTokenResponse(
    @SerializedName("access_token")
    val access_token: String?,
    @SerializedName("token_type")
    val token_type: String?
)

data class OrderResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
)

