package com.syntepro.sueldazo.ui.home.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.entity.service.Category
import com.syntepro.sueldazo.ui.shop.model.GiftCard
import com.syntepro.sueldazo.ui.shop.model.GiftCardRequest
import com.syntepro.sueldazo.ui.shop.model.ArticleRequest
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeService
@Inject constructor(retrofit: Retrofit) : HomeApi {
    private val baseApi by lazy { retrofit.create(HomeApi::class.java) }

    override fun getCategories(body: CategoryRequest): Call<BaseResponse<List<Category>>> = baseApi.getCategories(body)
    override fun getItems(body: ArticleRequest): Call<BaseResponse<List<ArticleResponse>>> = baseApi.getItems(body)
    override fun getParameters(body: ParameterRequest): Call<BaseResponse<ParameterResponse>> = baseApi.getParameters(body)
    override fun getFavorites(body: FavoriteRequest): Call<BaseResponse<List<FavoriteResponse>>> = baseApi.getFavorites(body)
    override fun getBanners(body: BannerRequest): Call<BaseResponse<List<BannerResponse>>> = baseApi.getBanners(body)
    override fun getPurchaseProducts(body: PurchasedProductsRequest): Call<BaseResponse<List<PurchasedProductsResponse>>> = baseApi.getPurchaseProducts(body)
    override fun getStates(body: StatesRequest): Call<BaseResponse<List<StatesResponse>>> = baseApi.getStates(body)
    override fun getGiftCards(body: GiftCardRequest): Call<BaseResponse<List<GiftCard>>> = baseApi.getGiftCards(body)
    override fun getFeaturedGiftCards(body: FeaturedGiftCardRequest): Call<BaseResponse<List<GiftCard>>> = baseApi.getFeaturedGiftCards(body)
    override fun getUserSavings(body: UserSavingsRequest): Call<BaseResponse<Double>> = baseApi.getUserSavings(body)

}
