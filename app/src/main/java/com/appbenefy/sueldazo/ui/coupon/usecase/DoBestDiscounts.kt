package com.appbenefy.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountRequest
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse
import com.appbenefy.sueldazo.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoBestDiscounts
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<BestDiscountResponse>>, DoBestDiscounts.Params>() {

    override suspend fun run(params: Params) = couponRepository.getBestDiscounts(params.request)

    data class Params(val request: BestDiscountRequest)
}