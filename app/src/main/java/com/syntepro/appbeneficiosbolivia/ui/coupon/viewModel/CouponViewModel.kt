package com.syntepro.appbeneficiosbolivia.ui.coupon.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.coupon.model.*
import com.syntepro.appbeneficiosbolivia.ui.coupon.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponViewModel
@Inject
constructor(
        private val doCouponList: DoCouponList,
        private val doCouponDetail: DoCouponDetail,
        private val doCouponAgency: DoCouponAgency,
        private val doUpdateCouponQuantity: DoUpdateCouponQuantity,
        private val doCouponRating: DoCouponRating,
        private val doBestDiscounts: DoBestDiscounts
): BaseViewModel() {

    val couponList: MutableLiveData<BaseResponse<List<CouponListResponse>>> = MutableLiveData()
    val couponDetail: MutableLiveData<BaseResponse<CouponDetailResponse>> = MutableLiveData()
    val couponAgency: MutableLiveData<BaseResponse<List<AgencyResponse>>> = MutableLiveData()
    val updateCouponQuantity: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    val saveRating: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
    val bestDiscounts: MutableLiveData<BaseResponse<List<BestDiscountResponse>>> = MutableLiveData()

    fun getCouponList(request: CouponListRequest) =
            doCouponList(DoCouponList.Params(request)) {
                it.fold(::handleFailure, ::handleCouponList)
            }

    fun getCouponDetail(request: CouponDetailRequest) =
            doCouponDetail(DoCouponDetail.Params(request)) {
                it.fold(::handleFailure, ::handleCouponDetail)
            }

    fun loadCouponAgency(request: CouponAgencyRequest) =
            doCouponAgency(DoCouponAgency.Params(request)) {
                it.fold(::handleFailure, ::handleCouponAgency)
            }

    fun updateUserCouponQuantity(request: UpdateCouponQuantityRequest) =
            doUpdateCouponQuantity(DoUpdateCouponQuantity.Params(request)) {
                it.fold(::handleFailure, ::handleUpdateCouponQuantity)
            }

    fun saveCouponRating(request: SaveCouponRatingRequest) =
            doCouponRating(DoCouponRating.Params(request)) {
                it.fold(::handleFailure, ::handleCouponRating)
            }

    fun getBestDiscounts(request: BestDiscountRequest) =
            doBestDiscounts(DoBestDiscounts.Params(request)) {
                it.fold(::handleFailure, ::handleBestDiscounts)
            }

    private fun handleCouponList(response: BaseResponse<List<CouponListResponse>>) {
        this.couponList.value = response
    }

    private fun handleCouponDetail(response: BaseResponse<CouponDetailResponse>) {
        this.couponDetail.value = response
    }

    private fun handleCouponAgency(response: BaseResponse<List<AgencyResponse>>) {
        this.couponAgency.value = response
    }

    private fun handleUpdateCouponQuantity(response: BaseResponse<Boolean>) {
        this.updateCouponQuantity.value = response
    }

    private fun handleCouponRating(response: BaseResponse<Boolean>) {
        this.saveRating.value = response
    }

    private fun handleBestDiscounts(response: BaseResponse<List<BestDiscountResponse>>) {
        this.bestDiscounts.value = response
    }

}