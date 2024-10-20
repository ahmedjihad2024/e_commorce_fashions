package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UserRegisterReq
import com.example.e_commorce_fashions.domain.models.UserRegisterResult
import com.example.e_commorce_fashions.domain.repository.Repository


class UserRegisterResultUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, UserRegisterResult>, UserRegisterReq>{
    override suspend fun execute(input: UserRegisterReq): Either<Failure<Nothing>, UserRegisterResult> {
        return repository.registerUser(input)
    }
}