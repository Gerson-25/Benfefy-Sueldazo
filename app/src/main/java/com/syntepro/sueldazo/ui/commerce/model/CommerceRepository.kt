package com.syntepro.sueldazo.ui.commerce.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import javax.inject.Inject

interface CommerceRepository {

    fun getBranches(request: BranchRequest): Either<Failure, BaseResponse<List<BranchResponse>>>
    fun getCommerceByBranch(request: CommerceByBranchRequest): Either<Failure, BaseResponse<List<CommerceByBranchResponse>>>
    fun getCommerceFiltered(request: CommerceFilterRequest): Either<Failure, BaseResponse<List<CommerceFilterResponse>>>
    fun getCommerceDetail(request: CommerceDetailRequest): Either<Failure, BaseResponse<CommerceDetailResponse>>
    fun getCommerceAgency(request: CommerceAgencyRequest): Either<Failure, BaseResponse<List<AgencyResponse>>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: CommerceService
    ) : CommerceRepository, BaseRepository() {

        override fun getBranches(request: BranchRequest): Either<Failure, BaseResponse<List<BranchResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBranch(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCommerceByBranch(request: CommerceByBranchRequest): Either<Failure, BaseResponse<List<CommerceByBranchResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCommerceByBranch(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCommerceFiltered(request: CommerceFilterRequest): Either<Failure, BaseResponse<List<CommerceFilterResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getFilteredCommerce(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCommerceDetail(request: CommerceDetailRequest): Either<Failure, BaseResponse<CommerceDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCommerceDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getCommerceAgency(request: CommerceAgencyRequest): Either<Failure, BaseResponse<List<AgencyResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getCommerceAgency(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }
}