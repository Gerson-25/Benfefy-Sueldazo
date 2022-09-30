package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ApiConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CouponApi {
    companion object {
        private const val COUPON_LIST = "Coupon/GetCoupons"
        private const val COUPON_DETAIL = "Coupon/GetCouponDetail"
        private const val COUPON_AGENCY = "Coupon/GetCouponAgencyList"
        private const val UPDATE_USER_COUPON_QUANTITY = "Coupon/UpdateUserCuponQuantity"
        private const val SAVE_COUPON_RATING = "Calification/SaveCouponCalification"
        private const val BEST_DISCOUNTS = "BestDiscounts/GetBestDiscounts"
        private const val FEATURED_COUPONS = "BestDiscounts/GetHighlightCoupons"
    }

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COUPON_LIST)
    fun getCouponList(
            @Body body: CouponListRequest
    ): Call<BaseResponse<List<CouponListResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COUPON_DETAIL)
    fun getCouponDetail(
            @Body body: CouponDetailRequest
    ): Call<BaseResponse<CouponDetailResponse>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COUPON_AGENCY)
    fun getCouponAgency(
            @Body body: CouponAgencyRequest
    ): Call<BaseResponse<List<AgencyResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(UPDATE_USER_COUPON_QUANTITY)
    fun updateCouponQuantity(
            @Body body: UpdateCouponQuantityRequest
    ): Call<BaseResponse<Boolean>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(SAVE_COUPON_RATING)
    fun saveCouponRating(
            @Body body: SaveCouponRatingRequest
    ): Call<BaseResponse<Boolean>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(BEST_DISCOUNTS)
    fun getBestDiscounts(
            @Body body: BestDiscountRequest
    ): Call<BaseResponse<List<BestDiscountResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(FEATURED_COUPONS)
    fun getFeaturedCoupons(
            @Body body: FeaturedCouponRequest
    ): Call<BaseResponse<List<FeaturedCouponResponse>>>
}