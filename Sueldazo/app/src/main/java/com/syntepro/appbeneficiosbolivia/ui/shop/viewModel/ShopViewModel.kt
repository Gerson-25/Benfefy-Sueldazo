package com.syntepro.appbeneficiosbolivia.ui.shop.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.shop.usecase.DoGiftDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopViewModel
@Inject
constructor(
        private val doGiftDetail: DoGiftDetail
): BaseViewModel() {

    val giftDetail: MutableLiveData<BaseResponse<GiftDetailResponse>> = MutableLiveData()

    fun getGiftDetail(request: GiftDetailRequest) =
            doGiftDetail(DoGiftDetail.Params(request)) {
                it.fold(::handleFailure, ::handleGiftDetail)
            }

    private fun handleGiftDetail(response: BaseResponse<GiftDetailResponse>) {
        this.giftDetail.value = response
    }

}