package com.syntepro.appbeneficiosbolivia.ui.shop.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopService
@Inject constructor(retrofit: Retrofit) : ShopApi {
    private val shopApiApi by lazy { retrofit.create(ShopApi::class.java) }

    override fun getGiftDetail(body: GiftDetailRequest): Call<BaseResponse<GiftDetailResponse>> = shopApiApi.getGiftDetail(body)

}