package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.UnlinkPlanRequest
import javax.inject.Inject

class DoUnlinkPlan
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<Boolean>, DoUnlinkPlan.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.unlinkPlan(params.request)

    data class Params(val request: UnlinkPlanRequest)
}