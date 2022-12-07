package com.appbenefy.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.coupon.model.CouponRepository
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponRequest
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponResponse
import javax.inject.Inject

class DoFeaturedCoupons
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<FeaturedCouponResponse>>, DoFeaturedCoupons.Params>() {

    override suspend fun run(params: Params) = couponRepository.getFeaturedCoupons(params.request)

    data class Params(val request: FeaturedCouponRequest)
}