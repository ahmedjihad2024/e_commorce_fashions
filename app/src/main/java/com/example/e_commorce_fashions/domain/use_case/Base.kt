package com.example.e_commorce_fashions.domain.use_case


interface UseCase<out Out, in In>{
    suspend fun execute(input: In): Out
}

//interface UseCase<out Out, in In>{
//    fun execute(input: In): Out
//}