package com.syntepro.appbeneficiosbolivia.ui.shop.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ShopApi {
    companion object {
        private const val GIFT_DETAIL = "PresentCoupon/GetPresentCouponDetail"
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(GIFT_DETAIL)
    fun getGiftDetail(
            @Body body: GiftDetailRequest
    ): Call<BaseResponse<GiftDetailResponse>>
}