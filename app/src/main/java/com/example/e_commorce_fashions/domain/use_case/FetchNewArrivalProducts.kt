package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.ProductsReq
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.repository.Repository

class FetchNewArrivalProductsUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, Products>, ProductsReq> {
    override suspend fun execute(req: ProductsReq): Either<Failure<Nothing>, Products> {
        return repository.fetchNewArrivalProducts(req)
    }
}