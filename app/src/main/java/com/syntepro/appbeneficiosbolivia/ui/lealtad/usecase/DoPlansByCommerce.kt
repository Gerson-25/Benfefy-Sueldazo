package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import javax.inject.Inject

class DoPlansByCommerce
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<List<PlanByCommerceResponse>>, DoPlansByCommerce.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getPlansByCommerce(params.request)

    data class Params(val request: PlanByCommerceRequest)
}
