package com.syntepro.appbeneficiosbolivia.accounts.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseRequest
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import retrofit2.Call
import javax.inject.Inject

interface AccountsRepository {
    fun getCustomer(token: String, request: BaseRequest<Nothing>): Either<Failure, BaseResponse<Nothing>>

    class Network
    @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val service: AccountsService
    ) : AccountsRepository {

        override fun getCustomer(
            token: String,
            request: BaseRequest<Nothing>
        ): Either<Failure, BaseResponse<Nothing>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCustomer(token, request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }


        private fun <T, R> request(
            call: Call<T>,
            transform: (T) -> R,
            default: T
        ): Either<Failure, R> {
            return try {
                val response = call.execute()
                when (response.isSuccessful) {
                    true -> Either.Right(transform((response.body() ?: default)))
                    false -> Either.Left(
                        Failure(
                            response.errorBody()?.string() ?: ""
                        )
                    )
                }
            } catch (exception: Throwable) {
                Either.Left(Failure(exception.message ?: exception.toString()))
            }
        }
    }
}
