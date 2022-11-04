package com.syntepro.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponDetailRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponDetailResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoCouponDetail
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<CouponDetailResponse>, DoCouponDetail.Params>() {

    override suspend fun run(params: Params) = couponRepository.getCouponDetail(params.request)

    data class Params(val request: CouponDetailRequest)
}