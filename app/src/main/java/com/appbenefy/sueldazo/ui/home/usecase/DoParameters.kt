package com.appbenefy.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.home.model.HomeRepository
import com.appbenefy.sueldazo.ui.home.model.ParameterRequest
import com.appbenefy.sueldazo.ui.home.model.ParameterResponse
import javax.inject.Inject

class DoParameters
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<ParameterResponse>, DoParameters.Params>() {

    override suspend fun run(params: Params) = homeRepository.getParameters(params.request)

    data class Params(val request: ParameterRequest)
}