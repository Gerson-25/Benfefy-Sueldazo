package com.appbenefy.sueldazo.ui.home.model

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.entity.service.*
import com.appbenefy.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HomeApi {
    companion object {
        private const val METHOD_NAME = "BusinessLine/GetCategories"
        private const val ITEMS = "Item/GetItems"
        private const val PARAMETERS = "Configuration/GetParametersCountry"
        private const val FAVORITES = "Favorite/GetFavorites"
        private const val BANNERS = "BusinessLine/GetBannerList"
        private const val PURCHASE_PRODUCTS = "Purchase/GetPurchasedProducts"
        private const val STATES = "Country/GetStates"
        private const val GIFT_CARDS = "Giftcard/GetGiftcards"
        private const val FEATURED_GIFT_CARDS = "Giftcard/GetHighlightGiftcards"
        private const val USER_SAVINGS = "User/GetUserSavings"
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(METHOD_NAME)
    fun getCategories(@Body body: CategoryRequest): Call<BaseResponse<List<Category>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(PARAMETERS)
    fun getParameters(@Body body: ParameterRequest):
            Call<BaseResponse<ParameterResponse>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(FAVORITES)
    fun getFavorites(@Body body: FavoriteRequest):
            Call<BaseResponse<List<FavoriteResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(BANNERS)
    fun getBanners(@Body body: BannerRequest):
            Call<BaseResponse<List<BannerResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(PURCHASE_PRODUCTS)
    fun getPurchaseProducts(@Body body: PurchasedProductsRequest):
            Call<BaseResponse<List<PurchasedProductsResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(STATES)
    fun getStates(@Body body: StatesRequest):
            Call<BaseResponse<List<StatesResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(USER_SAVINGS)
    fun getUserSavings(@Body body: UserSavingsRequest): Call<BaseResponse<Double>>
}