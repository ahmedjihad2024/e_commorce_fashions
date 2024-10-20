package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.domain.models.Favorites
import com.example.e_commorce_fashions.domain.repository.Repository
import kotlinx.coroutines.Job

class FavoritesListenerUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, Job>,  (favorites: Favorites) -> Unit> {
    override suspend fun execute(input: (favorites: Favorites) -> Unit): Either<Failure<Nothing>, Job> {
        return repository.favoritesListener(input)
    }
}