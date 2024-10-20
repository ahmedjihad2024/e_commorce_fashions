package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.PaymentIntentReq
import com.example.e_commorce_fashions.domain.models.ClientSecret
import com.example.e_commorce_fashions.domain.repository.Repository

class CreatePaymentIntentUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, ClientSecret>, PaymentIntentReq> {
    override suspend fun execute(input: PaymentIntentReq): Either<Failure<Nothing>, ClientSecret> {
        return repository.createPaymentIntent(input)
    }
}