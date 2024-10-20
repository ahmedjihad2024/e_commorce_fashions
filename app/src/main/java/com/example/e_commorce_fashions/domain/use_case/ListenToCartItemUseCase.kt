package com.example.e_commorce_fashions.domain.use_case

import com.example.e_commorce_fashions.app.utils.Either
import com.example.e_commorce_fashions.data.datasrouce.remote.Failure
import com.example.e_commorce_fashions.data.requests.ItemReferenceReq
import com.example.e_commorce_fashions.data.requests.ItemReferenceWithOnDataChangeReq
import com.example.e_commorce_fashions.domain.repository.Repository
import com.google.firebase.database.ValueEventListener

class ListenToCartItemUseCase(private val repository: Repository) : UseCase<Either<Failure<Nothing>, ValueEventListener>, ItemReferenceWithOnDataChangeReq>{
    override suspend fun execute(input: ItemReferenceWithOnDataChangeReq): Either<Failure<Nothing>, ValueEventListener> {
        return repository.listenToCartItem(input)
    }
}