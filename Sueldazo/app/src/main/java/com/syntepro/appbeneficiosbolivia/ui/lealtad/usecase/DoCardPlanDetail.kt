package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import javax.inject.Inject

class DoCardPlanDetail
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<PlanCardDetailResponse>, DoCardPlanDetail.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getCardPlanDetail(params.request)

    data class Params(val request: PlanByUserDetailRequest)
}
