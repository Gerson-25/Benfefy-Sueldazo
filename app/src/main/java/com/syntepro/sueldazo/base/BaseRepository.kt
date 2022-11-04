package com.syntepro.sueldazo.base

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.syntepro.sueldazo.service.ApiConfig.UNAUTHORIZED_RESPONSE
import retrofit2.Call

open class BaseRepository {
    fun <T, R> request(
            call: Call<T>,
            transform: (T) -> R,
            default: T
    ): Either<Failure, R> {
        return try {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> Either.Right(transform((response.body() ?: default)))
                false -> Either.Left(
                        if (response.code() == UNAUTHORIZED_RESPONSE)
                            Failure.Unauthorized else Failure(
                                response.errorBody()?.string() ?: ""
                        )
                )
            }
        } catch (exception: Throwable) {
            Either.Left(Failure(exception.message ?: exception.toString()))
        }
    }
}