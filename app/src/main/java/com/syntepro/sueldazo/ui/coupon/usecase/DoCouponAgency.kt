package com.syntepro.sueldazo.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import com.syntepro.sueldazo.ui.coupon.model.CouponAgencyRequest
import com.syntepro.sueldazo.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoCouponAgency
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<AgencyResponse>>, DoCouponAgency.Params>() {

    override suspend fun run(params: Params) = couponRepository.getCouponAgency(params.request)

    data class Params(val request: CouponAgencyRequest)
}