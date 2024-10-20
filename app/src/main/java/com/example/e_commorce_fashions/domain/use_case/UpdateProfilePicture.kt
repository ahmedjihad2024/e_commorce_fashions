package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UriReq
import com.example.e_commorce_fashions.domain.repository.Repository

class UpdateProfilePicture(val repository: Repository): UseCase<Either<Failure<Nothing>, Unit>, UriReq> {
    override suspend fun execute(input: UriReq): Either<Failure<Nothing>, Unit> {
        return repository.updateProfilePicture(input)
    }
}