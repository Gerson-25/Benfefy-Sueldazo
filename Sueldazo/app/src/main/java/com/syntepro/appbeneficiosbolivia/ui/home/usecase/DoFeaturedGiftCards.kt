package com.syntepro.appbeneficiosbolivia.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.FeaturedGiftCardRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import javax.inject.Inject

class DoFeaturedGiftCards
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<GiftCard>>, DoFeaturedGiftCards.Params>() {

    override suspend fun run(params: Params) = homeRepository.getFeaturedGiftCards(params.request)

    data class Params(val request: FeaturedGiftCardRequest)
}