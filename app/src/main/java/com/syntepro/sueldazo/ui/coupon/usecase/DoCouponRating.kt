package com.syntepro.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponRepository
import com.syntepro.sueldazo.ui.coupon.model.SaveCouponRatingRequest
import javax.inject.Inject

class DoCouponRating
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<Boolean>, DoCouponRating.Params>() {

    override suspend fun run(params: Params) = couponRepository.saveCouponRating(params.request)

    data class Params(val request: SaveCouponRatingRequest)
}