package com.syntepro.sueldazo.ui.shop.viewModel

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.syntepro.sueldazo.ui.shop.model.ArticleRequest
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import com.syntepro.sueldazo.service.BaseDataSourceFactory
import com.syntepro.sueldazo.service.NetworkService2
import com.syntepro.sueldazo.service.RetrofitClientInstance
import com.syntepro.sueldazo.service.ServiceStatus
import com.syntepro.sueldazo.utils.Constants

class DiscountViewModel: ViewModel() {
    private val parameters = MutableLiveData<ArticleRequest>()
    private var dsFactory: BaseDataSourceFactory<ArticleResponse>? = null
    private val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)

    private val config: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(Constants.LIST_PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

    private val ret = Transformations.map(parameters) { pr ->
        LivePagedListBuilder(dsFactory!!, config).build()
    }

    val items: LiveData<PagedList<ArticleResponse>> = Transformations.switchMap(ret) { it }

    fun netWorkStatus(): LiveData<ServiceStatus> = Transformations.switchMap(dsFactory!!.dsLiveData) {
        it.status
    }

    fun getItems(request: ArticleRequest) {
        parameters.value = request
    }
}