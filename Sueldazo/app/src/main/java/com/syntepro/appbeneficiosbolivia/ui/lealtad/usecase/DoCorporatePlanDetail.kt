package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanCorporateDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.PlanByUserDetailRequest
import javax.inject.Inject

class DoCorporatePlanDetail
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<PlanCorporateDetailResponse>, DoCorporatePlanDetail.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getCorporatePlanDetail(params.request)

    data class Params(val request: PlanByUserDetailRequest)
}
