package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.EphemeralKeyReq
import com.example.e_commorce_fashions.domain.models.EphemeralKey
import com.example.e_commorce_fashions.domain.repository.Repository

class GetEphemeralKeyUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, EphemeralKey>, EphemeralKeyReq> {
    override suspend fun execute(input: EphemeralKeyReq): Either<Failure<Nothing>, EphemeralKey> {
        return repository.getEphemeralKey(input)
    }
}