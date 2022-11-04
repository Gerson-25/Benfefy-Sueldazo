package com.syntepro.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponListRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponListResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoCouponList
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<CouponListResponse>>, DoCouponList.Params>() {

    override suspend fun run(params: Params) = couponRepository.getCouponList(params.request)

    data class Params(val request: CouponListRequest)
}