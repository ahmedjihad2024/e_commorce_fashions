package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.CartReq
import com.example.e_commorce_fashions.domain.repository.Repository

class ListenToCartUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, Unit>, CartReq> {
    override suspend fun execute(input: CartReq): Either<Failure<Nothing>, Unit> {
       return repository.listenToCart(input)
    }
}