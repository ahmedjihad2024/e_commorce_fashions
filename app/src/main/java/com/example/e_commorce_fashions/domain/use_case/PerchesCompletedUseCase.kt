package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.PerchesCompletedReq
import com.example.e_commorce_fashions.domain.repository.Repository

class PerchesCompletedUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, Unit>, PerchesCompletedReq>{
    override suspend fun execute(input: PerchesCompletedReq): Either<Failure<Nothing>, Unit> {
        return repository.perchesCompleted(input)
    }
}