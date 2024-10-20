package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.OrderReq
import com.example.e_commorce_fashions.domain.models.MyOrders
import com.example.e_commorce_fashions.domain.repository.Repository

class GetOrdersUseCase(
    private val repository: Repository
): UseCase<Either<Failure<Nothing>, MyOrders>, OrderReq> {
    override suspend fun execute(input: OrderReq): Either<Failure<Nothing>, MyOrders> {
        return repository.getOrders(input)
    }
}