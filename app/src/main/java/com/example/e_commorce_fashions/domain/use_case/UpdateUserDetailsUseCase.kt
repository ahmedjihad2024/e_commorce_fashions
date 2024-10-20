package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.UserDetailsReq
import com.example.e_commorce_fashions.domain.repository.Repository

class UpdateUserDetailsUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, Unit>, UserDetailsReq> {
    override suspend fun execute(input: UserDetailsReq): Either<Failure<Nothing>, Unit> {
        return repository.updateUserDetails(input)
    }
}