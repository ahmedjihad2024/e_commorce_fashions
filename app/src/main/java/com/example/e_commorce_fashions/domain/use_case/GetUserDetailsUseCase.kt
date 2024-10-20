package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.domain.models.UserDetails
import com.example.e_commorce_fashions.domain.repository.Repository

class GetUserDetailsUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, UserDetails>, Unit> {
    override suspend fun execute(input: Unit): Either<Failure<Nothing>, UserDetails> {
        return repository.getUserDetails()
    }
}