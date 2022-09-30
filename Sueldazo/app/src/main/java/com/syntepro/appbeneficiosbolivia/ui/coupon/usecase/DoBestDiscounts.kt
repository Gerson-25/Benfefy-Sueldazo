package com.syntepro.appbeneficiosbolivia.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.BestDiscountResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoBestDiscounts
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<BestDiscountResponse>>, DoBestDiscounts.Params>() {

    override suspend fun run(params: Params) = couponRepository.getBestDiscounts(params.request)

    data class Params(val request: BestDiscountRequest)
}