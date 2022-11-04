package com.syntepro.sueldazo.ui.agency.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import javax.inject.Inject

interface AgencyRepository {

    fun getAgencyDetail(request: AgencyDetailRequest): Either<Failure, BaseResponse<AgencyDetailResponse>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: AgencyService
    ) : AgencyRepository, BaseRepository() {

        override fun getAgencyDetail(request: AgencyDetailRequest): Either<Failure, BaseResponse<AgencyDetailResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getAgencyDetail(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }

}