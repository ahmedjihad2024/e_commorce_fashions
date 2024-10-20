package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.domain.repository.Repository

class RemoveFromCartUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, Unit>, ItemReferenceReq>{
    override suspend fun execute(input: ItemReferenceReq): Either<Failure<Nothing>, Unit> {
        return repository.removeFromCart(input)
    }
}