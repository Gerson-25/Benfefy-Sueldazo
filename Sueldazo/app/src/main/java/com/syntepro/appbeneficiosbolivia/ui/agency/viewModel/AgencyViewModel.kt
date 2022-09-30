package com.syntepro.appbeneficiosbolivia.ui.agency.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.usecase.DoAgencyDetail
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.*
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.*
import com.syntepro.appbeneficiosbolivia.ui.commerce.usecase.DoCommerceAgency
import com.syntepro.appbeneficiosbolivia.ui.coupon.usecase.DoCouponAgency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgencyViewModel
@Inject constructor(
        private val doCommerceAgency: DoCommerceAgency,
        private val doCouponAgency: DoCouponAgency,
        private val doAgencyDetail: DoAgencyDetail
) : BaseViewModel() {

    val commerceAgency: MutableLiveData<BaseResponse<List<AgencyResponse>>> = MutableLiveData()
    val couponAgency: MutableLiveData<BaseResponse<List<AgencyResponse>>> = MutableLiveData()
    val agencyDetail: MutableLiveData<BaseResponse<AgencyDetailResponse>> = MutableLiveData()

    fun loadCommerceAgency(request: CommerceAgencyRequest) =
            doCommerceAgency(DoCommerceAgency.Params(request)) {
                it.fold(::handleFailure, ::handleCommerceAgency)
            }

    fun loadCouponAgency(request: CouponAgencyRequest) =
            doCouponAgency(DoCouponAgency.Params(request)) {
                it.fold(::handleFailure, ::handleCouponAgency)
            }

    fun loadAgencyDetail(request: AgencyDetailRequest) =
            doAgencyDetail(DoAgencyDetail.Params(request)) {
                it.fold(::handleFailure, ::handleAgencyDetail)
            }

    private fun handleCommerceAgency(response: BaseResponse<List<AgencyResponse>>) {
        this.commerceAgency.value = response
    }

    private fun handleCouponAgency(response: BaseResponse<List<AgencyResponse>>) {
        this.couponAgency.value = response
    }

    private fun handleAgencyDetail(response: BaseResponse<AgencyDetailResponse>) {
        this.agencyDetail.value = response
    }

}