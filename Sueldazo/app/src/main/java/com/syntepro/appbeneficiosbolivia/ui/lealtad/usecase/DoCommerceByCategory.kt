package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import javax.inject.Inject

class DoCommerceByCategory
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<List<Commerce>>, DoCommerceByCategory.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getCommerceByCategory(params.request)

    data class Params(val request: CommerceRequest)
}
