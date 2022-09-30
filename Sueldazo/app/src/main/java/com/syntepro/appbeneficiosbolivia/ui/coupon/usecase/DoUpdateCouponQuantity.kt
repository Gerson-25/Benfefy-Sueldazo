package com.syntepro.appbeneficiosbolivia.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponRepository
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.UpdateCouponQuantityRequest
import javax.inject.Inject

class DoUpdateCouponQuantity
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<Boolean>, DoUpdateCouponQuantity.Params>() {

    override suspend fun run(params: Params) = couponRepository.updateCouponUserQuantity(params.request)

    data class Params(val request: UpdateCouponQuantityRequest)
}