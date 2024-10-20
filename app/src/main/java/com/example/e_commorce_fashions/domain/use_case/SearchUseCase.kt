package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.SearchReq
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.repository.Repository

class SearchUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, Products>, SearchReq> {
    override suspend fun execute(input: SearchReq): Either<Failure<Nothing>, Products> {
        return repository.search(input)
    }
}