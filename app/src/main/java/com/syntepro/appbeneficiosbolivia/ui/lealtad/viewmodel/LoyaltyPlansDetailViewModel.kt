package com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel

import androidx.lifecycle.MutableLiveData
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyPlansDetailViewModel
@Inject constructor(
        private val doCardPlanDetail: DoCardPlanDetail,
        private val doCorporatePlanDetail: DoCorporatePlanDetail,
        private val doMilesPlanDetail: DoMilesPlanDetail
) : BaseViewModel() {

    var card: MutableLiveData<BaseResponse<PlanCardDetailResponse>> = MutableLiveData()
    var miles: MutableLiveData<BaseResponse<PlanMilesDetailResponse>> = MutableLiveData()
    var corporate: MutableLiveData<BaseResponse<PlanCorporateDetailResponse>> = MutableLiveData()

    fun cardPlanDetail(request: PlanByUserDetailRequest) =
            doCardPlanDetail(DoCardPlanDetail.Params(request)) {
                it.fold(::handleFailure, ::handleCardPlan)
            }

    fun corporatePlanDetail(request: PlanByUserDetailRequest) =
            doCorporatePlanDetail(DoCorporatePlanDetail.Params(request)) {
                it.fold(::handleFailure, ::handleCorporatePlan)
            }
    fun milesPlanDetail(request: PlanByUserDetailRequest) =
            doMilesPlanDetail(DoMilesPlanDetail.Params(request)) {
                it.fold(::handleFailure, ::handleMilesPlan)
            }

    private fun handleCardPlan(response: BaseResponse<PlanCardDetailResponse>) {
        this.card.value = response
    }

    private fun handleCorporatePlan(response: BaseResponse<PlanCorporateDetailResponse>) {
        this.corporate.value = response
    }

    private fun handleMilesPlan(response: BaseResponse<PlanMilesDetailResponse>) {
        this.miles.value = response
    }

}