package com.syntepro.sueldazo.ui.sudamericana.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import javax.inject.Inject

interface SudamericanaRepository {

    fun saveSurvey(request: SurveyRequest): Either<Failure, BaseResponse<Boolean>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: SudamericanaService
    ) : SudamericanaRepository, BaseRepository() {

        override fun saveSurvey(request: SurveyRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.saveSurvey(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}