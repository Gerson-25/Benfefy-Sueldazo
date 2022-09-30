package com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import com.merckers.core.interactor.UseCase
import javax.inject.Inject

class DoCardDetail
@Inject constructor(private val loyaltyPlanRepository: LoyaltyPlanRepository) : UseCase<BaseResponse<CardDetailResponse>, DoCardDetail.Params>() {

    override suspend fun run(params: Params) = loyaltyPlanRepository.getCardDetail(params.request)

    data class Params(val request: CardDetailRequest)
}