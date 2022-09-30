package com.syntepro.appbeneficiosbolivia.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository
import com.syntepro.appbeneficiosbolivia.ui.home.model.UserSavingsRequest
import javax.inject.Inject

class DoUserSavings
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<Double>, DoUserSavings.Params>() {

    override suspend fun run(params: Params) = homeRepository.getUserSavings(params.request)

    data class Params(val request: UserSavingsRequest)
}