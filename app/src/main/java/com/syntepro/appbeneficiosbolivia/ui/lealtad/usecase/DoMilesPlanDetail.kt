package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByUserDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanMilesDetailResponse
import javax.inject.Inject

class DoMilesPlanDetail
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<PlanMilesDetailResponse>, DoMilesPlanDetail.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getMilesPlanDetail(params.request)

    data class Params(val request: PlanByUserDetailRequest)
}
