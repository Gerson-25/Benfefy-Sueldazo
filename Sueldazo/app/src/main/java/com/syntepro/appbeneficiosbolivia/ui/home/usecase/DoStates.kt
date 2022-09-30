package com.syntepro.appbeneficiosbolivia.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository
import com.syntepro.appbeneficiosbolivia.ui.home.model.StatesRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.StatesResponse
import javax.inject.Inject

class DoStates
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<StatesResponse>>, DoStates.Params>() {

    override suspend fun run(params: Params) = homeRepository.getStates(params.request)

    data class Params(val request: StatesRequest)
}