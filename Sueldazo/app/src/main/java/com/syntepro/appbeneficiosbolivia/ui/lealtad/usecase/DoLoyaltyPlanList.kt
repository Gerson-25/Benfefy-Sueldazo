package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase


import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanListRequest
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanListResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.LoyaltyPlanRepository
import javax.inject.Inject

class DoLoyaltyPlanList
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<LoyaltyPlanListResponse>, DoLoyaltyPlanList.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getLoyaltyPlansByUser(params.request)

    data class Params(val request: LoyaltyPlanListRequest)
}
