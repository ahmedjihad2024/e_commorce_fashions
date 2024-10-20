package com.example.e_commorce_fashions.data.mapper

import com.example.e_commorce_fashions.data.requests.UserLoginReq
import com.example.e_commorce_fashions.data.responses.ClientSecretResponse
import com.example.e_commorce_fashions.data.responses.CustomerIdResponse
import com.example.e_commorce_fashions.data.responses.EphemeralKeyResponse
//
import com.example.e_commorce_fashions.data.responses.UserLoginResultResp
import com.example.e_commorce_fashions.data.responses.UserRegisterResultResp
import com.example.e_commorce_fashions.domain.models.ClientSecret
import com.example.e_commorce_fashions.domain.models.CustomerId
import com.example.e_commorce_fashions.domain.models.EphemeralKey
import com.example.e_commorce_fashions.domain.models.UserLoginResult
import com.example.e_commorce_fashions.domain.models.UserRegisterResult


val UserRegisterResultResp.toDomain get() = UserRegisterResult(
    uid = uid.orEmpty(),
)

val UserLoginResultResp.toDomain get() = UserLoginResult(
    uid = uid.orEmpty(),
)

val CustomerIdResponse.toDomain get() = CustomerId(
    customerId = id.orEmpty()
)

val EphemeralKeyResponse.toDomain get() = EphemeralKey(
    key = key.orEmpty()
)

val ClientSecretResponse.toDomain get() = ClientSecret(
    client_secret = client_secret.orEmpty()
)