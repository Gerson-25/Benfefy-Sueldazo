package com.appbenefy.sueldazo.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.merckers.core.platform.BaseViewModel
import com.appbenefy.sueldazo.entity.service.*
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountRequest
import com.appbenefy.sueldazo.ui.coupon.model.BestDiscountResponse
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponRequest
import com.appbenefy.sueldazo.ui.coupon.model.FeaturedCouponResponse
import com.appbenefy.sueldazo.ui.coupon.usecase.DoBestDiscounts
import com.appbenefy.sueldazo.ui.coupon.usecase.DoFeaturedCoupons
import com.appbenefy.sueldazo.ui.home.model.*
import com.appbenefy.sueldazo.ui.home.model.CategoryRequest
import com.appbenefy.sueldazo.ui.home.model.FavoriteRequest
import com.appbenefy.sueldazo.ui.home.model.StatesRequest
import com.appbenefy.sueldazo.ui.home.usecase.*
import com.appbenefy.sueldazo.ui.notifications.model.NotificationCountRequest
import com.appbenefy.sueldazo.ui.notifications.model.NotificationCountResponse
import com.appbenefy.sueldazo.ui.notifications.usecase.DoNotificationCount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewModel
@Inject constructor(
        private val doCategories: DoCategories,
        private val doParameters: DoParameters,
        private val doFavorites: DoFavorites,
        private val doNotificationCount: DoNotificationCount,
        private val doBestDiscounts: DoBestDiscounts,
        private val doBanners: DoBanners,
        private val doPurchaseProducts: DoPurchaseProducts,
        private val doStates: DoStates,
        private val doFeaturedCoupons: DoFeaturedCoupons,
        private val doUserSavings: DoUserSavings
) : BaseViewModel() {

    val categories: MutableLiveData<BaseResponse<List<Category>>> = MutableLiveData()
    val parameters: MutableLiveData<BaseResponse<ParameterResponse>> = MutableLiveData()
    val favorites: MutableLiveData<BaseResponse<List<FavoriteResponse>>> = MutableLiveData()
    val unlink: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    val counter: MutableLiveData<BaseResponse<NotificationCountResponse>> = MutableLiveData()
    val bestDiscounts: MutableLiveData<BaseResponse<List<BestDiscountResponse>>> = MutableLiveData()
    val banners: MutableLiveData<BaseResponse<List<BannerResponse>>> = MutableLiveData()
    val purchaseProducts: MutableLiveData<BaseResponse<List<PurchasedProductsResponse>>> = MutableLiveData()
    val states: MutableLiveData<BaseResponse<List<StatesResponse>>> = MutableLiveData()
    val featuredCoupons: MutableLiveData<BaseResponse<List<FeaturedCouponResponse>>> = MutableLiveData()
    val userSavings: MutableLiveData<BaseResponse<Double>> = MutableLiveData()

    fun loadCategories(request: CategoryRequest) =
            doCategories(DoCategories.Params(request)) {
                it.fold(::handleFailure, ::handleCategories)
            }

    fun loadParams(request: ParameterRequest) =
            doParameters(DoParameters.Params(request)) {
                it.fold(::handleFailure, ::handleParameters)
            }

    fun loadFavorites(request: FavoriteRequest) =
            doFavorites(DoFavorites.Params(request)) {
                it.fold(::handleFailure, ::handleFavorites)
            }

    fun notificationCounter(request: NotificationCountRequest) =
            doNotificationCount(DoNotificationCount.Params(request)) {
                it.fold(::handleFailure, ::handleCounter)
            }

    fun loadBestDiscounts(request: BestDiscountRequest) =
            doBestDiscounts(DoBestDiscounts.Params(request)) {
                it.fold(::handleFailure, ::handleBestDiscounts)
            }

    fun loadBanners(request: BannerRequest) =
            doBanners(DoBanners.Params(request)) {
                it.fold(::handleFailure, ::handleBanners)
            }

    fun loadPurchaseProducts(request: PurchasedProductsRequest) =
            doPurchaseProducts(DoPurchaseProducts.Params(request)) {
                it.fold(::handleFailure, ::handlePurchaseProducts)
            }

    fun loadStates(request: StatesRequest) =
            doStates(DoStates.Params(request)) {
                it.fold(::handleFailure, ::handleStates)
            }


    fun loadFeaturedCoupons(request: FeaturedCouponRequest) =
            doFeaturedCoupons(DoFeaturedCoupons.Params(request)) {
                it.fold(::handleFailure, ::handleFeaturedCoupons)
            }

    fun loadUserSavings(request: UserSavingsRequest) =
        doUserSavings(DoUserSavings.Params(request)) {
            it.fold(::handleFailure, ::handleUserSavings)
        }

    private fun handleCategories(response: BaseResponse<List<Category>>) {
        this.categories.value = response
    }

    private fun handleParameters(response: BaseResponse<ParameterResponse>) {
        this.parameters.value = response
    }

    private fun handleFavorites(response: BaseResponse<List<FavoriteResponse>>) {
        this.favorites.value = response
    }

    private fun handleUnlink(response: BaseResponse<Boolean>) {
        this.unlink.value = response
    }

    private fun handleCounter(response: BaseResponse<NotificationCountResponse>) {
        this.counter.value = response
    }

    private fun handleBestDiscounts(response: BaseResponse<List<BestDiscountResponse>>) {
        this.bestDiscounts.value = response
    }

    private fun handleBanners(response: BaseResponse<List<BannerResponse>>) {
        this.banners.value = response
    }

    private fun handlePurchaseProducts(response: BaseResponse<List<PurchasedProductsResponse>>) {
        this.purchaseProducts.value = response
    }

    private fun handleStates(response: BaseResponse<List<StatesResponse>>) {
        this.states.value = response
    }

    private fun handleFeaturedCoupons(response: BaseResponse<List<FeaturedCouponResponse>>) {
        this.featuredCoupons.value = response
    }

    private fun handleUserSavings(response: BaseResponse<Double>) {
        this.userSavings.value = response
    }

}