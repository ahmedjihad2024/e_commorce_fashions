package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.CartItemReq
import com.example.e_commorce_fashions.domain.repository.Repository

class AddToCartUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, Unit>, CartItemReq> {
    override suspend fun execute(input: CartItemReq): Either<Failure<Nothing>, Unit> {
       return repository.addToCart(input)
    }
}