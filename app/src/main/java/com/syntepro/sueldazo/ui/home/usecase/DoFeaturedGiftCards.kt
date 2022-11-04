package com.syntepro.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.model.FeaturedGiftCardRequest
import com.syntepro.sueldazo.ui.home.model.HomeRepository
import com.syntepro.sueldazo.ui.shop.model.GiftCard
import javax.inject.Inject

class DoFeaturedGiftCards
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<GiftCard>>, DoFeaturedGiftCards.Params>() {

    override suspend fun run(params: Params) = homeRepository.getFeaturedGiftCards(params.request)

    data class Params(val request: FeaturedGiftCardRequest)
}