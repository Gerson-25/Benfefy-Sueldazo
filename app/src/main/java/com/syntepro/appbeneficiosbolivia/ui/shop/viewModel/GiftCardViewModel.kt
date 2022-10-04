package com.syntepro.appbeneficiosbolivia.ui.shop.viewModel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCard
import com.syntepro.appbeneficiosbolivia.ui.shop.model.GiftCardRequest
import com.syntepro.appbeneficiosbolivia.service.BaseDataSourceFactory
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.service.ServiceStatus
import com.syntepro.appbeneficiosbolivia.utils.Constants

class GiftCardViewModel: ViewModel() {
    private val parameters = MutableLiveData<GiftCardRequest>()
    private var dsFactory: BaseDataSourceFactory<GiftCard>? = null
    private val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)

    private val config: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(Constants.LIST_PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

    private val ret = Transformations.map(parameters) { pr ->
        LivePagedListBuilder(dsFactory!!, config).build()
    }

    val giftCards: LiveData<PagedList<GiftCard>> = Transformations.switchMap(ret) { it }

    fun netWorkStatus(): LiveData<ServiceStatus> = Transformations.switchMap(dsFactory!!.dsLiveData) {
        it.status
    }

    fun getGiftCards(request: GiftCardRequest) {
        parameters.value = request
    }
}