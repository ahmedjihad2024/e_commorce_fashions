package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.domain.models.CustomerId
import com.example.e_commorce_fashions.domain.repository.Repository

class GetCustomerIdUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, CustomerId>, Unit> {
    override suspend fun execute(input: Unit): Either<Failure<Nothing>, CustomerId> {
        return repository.getCustomerId()
    }
}