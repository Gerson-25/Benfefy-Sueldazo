package com.syntepro.appbeneficiosbolivia.ui.lealtad.viewmodel

import androidx.lifecycle.MutableLiveData
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.*
import com.syntepro.appbeneficiosbolivia.ui.lealtad.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyPlanViewModel
@Inject constructor(
        private val doLoyaltyPlanList: DoLoyaltyPlanList,
        private val doCommerceByCategory: DoCommerceByCategory,
        private val doPlansByCommerce: DoPlansByCommerce,
        private val doAffiliateToPlan: DoAffiliateToPlan,
        private val doPlanDetail: DoPlanDetail,
        private val doCardDetail: DoCardDetail,
        private val doMilesGoals: DoMilesGoals
) : BaseViewModel() {
    var plans: MutableLiveData<BaseResponse<LoyaltyPlanListResponse>> = MutableLiveData()
    val commerce: MutableLiveData<BaseResponse<List<Commerce>>> = MutableLiveData()
    val plansCommerce: MutableLiveData<BaseResponse<List<PlanByCommerceResponse>>> = MutableLiveData()
    val affiliate: MutableLiveData<BaseResponse<AffiliateToPlanResponse>> = MutableLiveData()
    val planDetail: MutableLiveData<BaseResponse<PlanDetailResponse>> = MutableLiveData()
    val cardDetail: MutableLiveData<BaseResponse<CardDetailResponse>> = MutableLiveData()
    val milesGoals: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()

    init {
        affiliate.value = null
    }

    fun loyaltyPlans(request: LoyaltyPlanListRequest) =
            doLoyaltyPlanList(DoLoyaltyPlanList.Params(request)) {
                it.fold(::handleFailure, ::handlePlans)
            }

    fun commerceByCategory(request: CommerceRequest) =
            doCommerceByCategory(DoCommerceByCategory.Params(request)) {
                it.fold(::handleFailure, ::handleCommerce)
            }

    fun plansByCommerce(request: PlanByCommerceRequest) =
            doPlansByCommerce(DoPlansByCommerce.Params(request)) {
                it.fold(::handleFailure, ::handlePlansCommerce)
            }


    fun affiliateToPlan(request: AffiliateToPlanRequest) =
            doAffiliateToPlan(DoAffiliateToPlan.Params(request)) {
                it.fold(::handleFailure, ::affiliateToPlanReponse)
            }

    fun getPlanDetail(request: PlanDetailRequest) =
            doPlanDetail(DoPlanDetail.Params(request)) {
                it.fold(::handleFailure, ::planDetailResponse)
            }

    fun getCardDetail(request: CardDetailRequest) =
            doCardDetail(DoCardDetail.Params(request)) {
                it.fold(::handleFailure, ::cardDetailResponse)
            }

    fun saveMilesGoals(request: MilesGoalsRequest) =
            doMilesGoals(DoMilesGoals.Params(request)) {
                it.fold(::handleFailure, ::milesGoalsResponse)
            }

    private fun handlePlans(response: BaseResponse<LoyaltyPlanListResponse>) {
        this.plans.value = response
    }

    private fun handleCommerce(response: BaseResponse<List<Commerce>>) {
        this.commerce.value = response
    }

    private fun handlePlansCommerce(response: BaseResponse<List<PlanByCommerceResponse>>) {
        this.plansCommerce.value = response
    }

    private fun affiliateToPlanReponse(response: BaseResponse<AffiliateToPlanResponse>) {
        this.affiliate.value = response
    }

    private fun planDetailResponse(response: BaseResponse<PlanDetailResponse>?) {
        this.planDetail.value = response
    }

    private fun cardDetailResponse(response: BaseResponse<CardDetailResponse>?) {
        this.cardDetail.value = response
    }

    private fun milesGoalsResponse(response: BaseResponse<Boolean>?) {
        this.milesGoals.value = response
    }

}