package com.syntepro.sueldazo.ui.shop.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.shop.model.GiftDetailRequest
import com.syntepro.sueldazo.ui.shop.model.GiftDetailResponse
import com.syntepro.sueldazo.ui.shop.model.ShopRepository
import javax.inject.Inject

class DoGiftDetail
@Inject constructor(private val shopRepository: ShopRepository) : UseCase<BaseResponse<GiftDetailResponse>, DoGiftDetail.Params>() {

    override suspend fun run(params: Params) = shopRepository.getGiftDetail(params.request)

    data class Params(val request: GiftDetailRequest)
}