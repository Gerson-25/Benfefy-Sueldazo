package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.MilesGoalsRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.MilesGoalsResponse
import javax.inject.Inject

class DoMilesGoals
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<Boolean>, DoMilesGoals.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.saveMilesGoals(params.request)

    data class Params(val request: MilesGoalsRequest)
}