package com.appbenefy.sueldazo.ui.home.model

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.entity.service.Category
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeService
@Inject constructor(retrofit: Retrofit) : HomeApi {
    private val baseApi by lazy { retrofit.create(HomeApi::class.java) }

    override fun getCategories(body: CategoryRequest): Call<BaseResponse<List<Category>>> = baseApi.getCategories(body)
    override fun getParameters(body: ParameterRequest): Call<BaseResponse<ParameterResponse>> = baseApi.getParameters(body)
    override fun getFavorites(body: FavoriteRequest): Call<BaseResponse<List<FavoriteResponse>>> = baseApi.getFavorites(body)
    override fun getBanners(body: BannerRequest): Call<BaseResponse<List<BannerResponse>>> = baseApi.getBanners(body)
    override fun getPurchaseProducts(body: PurchasedProductsRequest): Call<BaseResponse<List<PurchasedProductsResponse>>> = baseApi.getPurchaseProducts(body)
    override fun getStates(body: StatesRequest): Call<BaseResponse<List<StatesResponse>>> = baseApi.getStates(body)
    override fun getUserSavings(body: UserSavingsRequest): Call<BaseResponse<Double>> = baseApi.getUserSavings(body)

}
