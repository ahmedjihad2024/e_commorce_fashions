package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UserLoginReq
import com.example.e_commorce_fashions.domain.models.UserLoginResult
import com.example.e_commorce_fashions.domain.repository.Repository

class LoginUserUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, UserLoginResult>, UserLoginReq> {
    override suspend fun execute(input: UserLoginReq): Either<Failure<Nothing>, UserLoginResult> {
        return repository.loginUser(input)
    }
}