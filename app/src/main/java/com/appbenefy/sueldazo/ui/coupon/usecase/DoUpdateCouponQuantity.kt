package com.appbenefy.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.coupon.model.CouponRepository
import com.appbenefy.sueldazo.ui.coupon.model.UpdateCouponQuantityRequest
import javax.inject.Inject

class DoUpdateCouponQuantity
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<Boolean>, DoUpdateCouponQuantity.Params>() {

    override suspend fun run(params: Params) = couponRepository.updateCouponUserQuantity(params.request)

    data class Params(val request: UpdateCouponQuantityRequest)
}