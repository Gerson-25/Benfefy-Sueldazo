package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponService
@Inject constructor(retrofit: Retrofit) : CouponApi {
    private val couponApiApi by lazy { retrofit.create(CouponApi::class.java) }

    override fun getCouponList(body: CouponListRequest): Call<BaseResponse<List<CouponListResponse>>> =couponApiApi.getCouponList(body)
    override fun getCouponDetail(body: CouponDetailRequest): Call<BaseResponse<CouponDetailResponse>> = couponApiApi.getCouponDetail(body)
    override fun getCouponAgency(body: CouponAgencyRequest): Call<BaseResponse<List<AgencyResponse>>> = couponApiApi.getCouponAgency(body)
    override fun updateCouponQuantity(body: UpdateCouponQuantityRequest): Call<BaseResponse<Boolean>> = couponApiApi.updateCouponQuantity(body)
    override fun saveCouponRating(body: SaveCouponRatingRequest): Call<BaseResponse<Boolean>> = couponApiApi.saveCouponRating(body)
    override fun getBestDiscounts(body: BestDiscountRequest): Call<BaseResponse<List<BestDiscountResponse>>> = couponApiApi.getBestDiscounts(body)
    override fun getFeaturedCoupons(body: FeaturedCouponRequest): Call<BaseResponse<List<FeaturedCouponResponse>>> = couponApiApi.getFeaturedCoupons(body)

}