package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UserLoginAuthTokenReq
import com.example.e_commorce_fashions.domain.models.UserLoginResult
import com.example.e_commorce_fashions.domain.repository.Repository

class LoginUserWithFacebookUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, UserLoginResult>, UserLoginAuthTokenReq>{
    override suspend fun execute(input: UserLoginAuthTokenReq): Either<Failure<Nothing>, UserLoginResult> {
        return repository.loginWithCredential(input)
    }
}