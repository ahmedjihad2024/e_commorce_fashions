package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.FavoritesReq
import com.example.e_commorce_fashions.domain.models.Products
import com.example.e_commorce_fashions.domain.repository.Repository

class GetFavoritesUseCase(private val repository: Repository): UseCase<Either<Failure<Nothing>, Products>, FavoritesReq> {
    override suspend fun execute(input: FavoritesReq): Either<Failure<Nothing>, Products> {
        return repository.getFavorites(input)
    }
}