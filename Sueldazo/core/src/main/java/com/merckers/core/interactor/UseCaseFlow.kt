package com.merckers.core.interactor

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class UseCaseFlow<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Flow<Either<Failure, Type>>

    operator fun invoke(params: Params, onResult: (Flow<Either<Failure, Type>>) -> Unit = {}) {
        val job = GlobalScope.async(Dispatchers.IO) { run(params) }
        GlobalScope.launch(Dispatchers.Main) { onResult(job.await()) }
    }
}
