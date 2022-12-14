package com.appbenefy.sueldazo.ui.profile.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.appbenefy.sueldazo.base.BaseRepository
import com.appbenefy.sueldazo.core.entities.BaseResponse
import retrofit2.Call
import javax.inject.Inject

interface ProfileRepository {

    fun getUserStats(request: UserStatsRequest): Either<Failure, BaseResponse<UserStatsResponse>>
    fun getUserTransactions(request: TransactionRequest): Either<Failure, BaseResponse<SavingDetailsResponse>>
    fun getTransactionDetail(request: TransactionDetailRequest): Either<Failure, BaseResponse<TransactionDetailResponse>>
    fun getSavingsResume(body: TransactionRequest): Either<Failure, BaseResponse<SavingResumeResponse>>

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

        override fun getUserTransactions(request: TransactionRequest): Either<Failure, BaseResponse<SavingDetailsResponse>> {
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

        override fun getSavingsResume(body: TransactionRequest): Either<Failure, BaseResponse<SavingResumeResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getSavingsResume(body), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }


    }

}