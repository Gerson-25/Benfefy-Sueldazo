package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyPlanListService
@Inject constructor(retrofit: Retrofit) : LoyaltyApi {
    private val loyaltyApiApi by lazy { retrofit.create(LoyaltyApi::class.java) }

    override fun getLoyaltyPlansByUser(body: LoyaltyPlanListRequest): Call<BaseResponse<LoyaltyPlanListResponse>> = loyaltyApiApi.getLoyaltyPlansByUser(body)

    override fun getCommerceByCategory(body: CommerceRequest): Call<BaseResponse<List<Commerce>>> = loyaltyApiApi.getCommerceByCategory(body)

    override fun getPlansByCommerce(body: PlanByCommerceRequest): Call<BaseResponse<List<PlanByCommerceResponse>>> = loyaltyApiApi.getPlansByCommerce(body)

    override fun affiliateToPlan(body: AffiliateToPlanRequest): Call<BaseResponse<AffiliateToPlanResponse>> = loyaltyApiApi.affiliateToPlan(body)

    override fun unlinkPlan(body: UnlinkPlanRequest): Call<BaseResponse<Boolean>> = loyaltyApiApi.unlinkPlan(body)

    override fun getCardPlanDetail(body: PlanByUserDetailRequest): Call<BaseResponse<PlanCardDetailResponse>> = loyaltyApiApi.getCardPlanDetail(body)

    override fun getCorporatelanDetail(body: PlanByUserDetailRequest): Call<BaseResponse<PlanCorporateDetailResponse>> = loyaltyApiApi.getCorporatelanDetail(body)

    override fun getMileslanDetail(body: PlanByUserDetailRequest): Call<BaseResponse<PlanMilesDetailResponse>> = loyaltyApiApi.getMileslanDetail(body)

    override fun getPlanlanDetail(body: PlanDetailRequest): Call<BaseResponse<PlanDetailResponse>>  = loyaltyApiApi.getPlanlanDetail(body)

    override fun getCardlanDetail(body: CardDetailRequest): Call<BaseResponse<CardDetailResponse>> = loyaltyApiApi.getCardlanDetail(body)

    override fun saveMilesGoals(body: MilesGoalsRequest): Call<BaseResponse<Boolean>> = loyaltyApiApi.saveMilesGoals(body)
}
