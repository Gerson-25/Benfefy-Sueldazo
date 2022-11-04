package com.syntepro.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.model.HomeRepository
import com.syntepro.sueldazo.ui.shop.model.GiftCard
import com.syntepro.sueldazo.ui.shop.model.GiftCardRequest
import javax.inject.Inject

class DoGiftCards
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<GiftCard>>, DoGiftCards.Params>() {

    override suspend fun run(params: Params) = homeRepository.getGiftCards(params.request)

    data class Params(val request: GiftCardRequest)
}