package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ApiConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoyaltyApi {
    companion object {
        private const val LOYALTY_PLANS_BY_USER = "LoyaltyPlan/GetPlansListByUser"
        private const val COMMERCE_BY_CATEGORY = "Commerce/GetCommerceListByBusinessLine"
        private const val PLANS_BY_COMMERCE = "LoyaltyPlan/GetPlansListByCommerce"
        private const val AFFILIATE_TO_PLAN = "LoyaltyPlan/AffiliateUserToPlan"
        private const val UNLINK_PLAN = "LoyaltyPlan/UnlinkUserFromPlan"
        private const val GET_CARD_PLAN = "LoyaltyPlan/GetCardUserPlan"
        private const val GET_MILES_PLAN = "LoyaltyPlan/GetMilesPlan"
        private const val GET_CORPORATE_PLAN = "LoyaltyPlan/GetCorporatePlan"
        private const val GET_PLAN_DETAIL = "LoyaltyPlan/GetPlanDetail"
        private const val GET_CARD_DETAIL = "LoyaltyPlan/GetCardDetail"
        private const val SAVE_MILES_GOALS = "LoyaltyPlan/SaveMilesGoal"
    }

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(LOYALTY_PLANS_BY_USER)
    fun getLoyaltyPlansByUser(
            @Body body: LoyaltyPlanListRequest
    ): Call<BaseResponse<LoyaltyPlanListResponse>>


    @POST(COMMERCE_BY_CATEGORY)
    fun getCommerceByCategory(
            @Body body: CommerceRequest
    ): Call<BaseResponse<List<Commerce>>>

    @POST(PLANS_BY_COMMERCE)
    fun getPlansByCommerce(
            @Body body: PlanByCommerceRequest
    ): Call<BaseResponse<List<PlanByCommerceResponse>>>

    @POST(AFFILIATE_TO_PLAN)
    fun affiliateToPlan(
            @Body body: AffiliateToPlanRequest
    ): Call<BaseResponse<AffiliateToPlanResponse>>

    @POST(UNLINK_PLAN)
    fun unlinkPlan(
            @Body body: UnlinkPlanRequest
    ): Call<BaseResponse<Boolean>>

    @POST(GET_CORPORATE_PLAN)
    fun getCorporatelanDetail(
            @Body body: PlanByUserDetailRequest
    ): Call<BaseResponse<PlanCorporateDetailResponse>>

    @POST(GET_CARD_PLAN)
    fun getCardPlanDetail(
            @Body body: PlanByUserDetailRequest
    ): Call<BaseResponse<PlanCardDetailResponse>>

    @POST(GET_MILES_PLAN)
    fun getMileslanDetail(
            @Body body: PlanByUserDetailRequest
    ): Call<BaseResponse<PlanMilesDetailResponse>>

    @POST(GET_PLAN_DETAIL)
    fun getPlanlanDetail(
            @Body body: PlanDetailRequest
    ): Call<BaseResponse<PlanDetailResponse>>

    @POST(GET_CARD_DETAIL)
    fun getCardlanDetail(
            @Body body: CardDetailRequest
    ): Call<BaseResponse<CardDetailResponse>>

    @POST(SAVE_MILES_GOALS)
    fun saveMilesGoals(
            @Body body: MilesGoalsRequest
    ): Call<BaseResponse<Boolean>>
}