package com.appbenefy.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.home.model.HomeRepository
import com.appbenefy.sueldazo.ui.home.model.StatesRequest
import com.appbenefy.sueldazo.ui.home.model.StatesResponse
import javax.inject.Inject

class DoStates
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<StatesResponse>>, DoStates.Params>() {

    override suspend fun run(params: Params) = homeRepository.getStates(params.request)

    data class Params(val request: StatesRequest)
}