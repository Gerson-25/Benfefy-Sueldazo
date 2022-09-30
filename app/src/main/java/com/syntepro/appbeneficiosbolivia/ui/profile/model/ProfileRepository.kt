package com.syntepro.appbeneficiosbolivia.ui.profile.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.appbeneficiosbolivia.base.BaseRepository
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import javax.inject.Inject

interface ProfileRepository {

    fun getUserStats(request: UserStatsRequest): Either<Failure, BaseResponse<UserStatsResponse>>
    fun getUserTransactions(request: TransactionRequest): Either<Failure, BaseResponse<List<TransactionResponse>>>
    fun getTransactionDetail(request: TransactionDetailRequest): Either<Failure, BaseResponse<TransactionDetailResponse>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: ProfileService
    ) : ProfileRepository, BaseRepository() {

        override fun getUserStats(request: UserStatsRequest): Either<Failure, BaseResponse<UserStatsResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUserStats(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getUserTransactions(request: TransactionRequest): Either<Failure, BaseResponse<List<TransactionResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getUserTransactions(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getTransactionDetail(request: TransactionDetailRequest): Either<Failure, BaseResponse<TransactionDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getTransactionDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }

}