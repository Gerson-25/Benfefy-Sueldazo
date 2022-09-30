package com.syntepro.appbeneficiosbolivia.ui.coupon.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponAgencyRequest
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponAgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.CouponRepository
import javax.inject.Inject

class DoCouponAgency
@Inject constructor(private val couponRepository: CouponRepository) : UseCase<BaseResponse<List<AgencyResponse>>, DoCouponAgency.Params>() {

    override suspend fun run(params: Params) = couponRepository.getCouponAgency(params.request)

    data class Params(val request: CouponAgencyRequest)
}