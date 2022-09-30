package com.syntepro.appbeneficiosbolivia.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponRepository
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.FeaturedCouponRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.FeaturedCouponResponse
import javax.inject.Inject

class DoFeaturedCoupons
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<FeaturedCouponResponse>>, DoFeaturedCoupons.Params>() {

    override suspend fun run(params: Params) = couponRepository.getFeaturedCoupons(params.request)

    data class Params(val request: FeaturedCouponRequest)
}