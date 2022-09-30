package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import javax.inject.Inject

class DoPlanDetail
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<PlanDetailResponse>, DoPlanDetail.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getPlanDetail(params.request)

    data class Params(val request: PlanDetailRequest)
}
