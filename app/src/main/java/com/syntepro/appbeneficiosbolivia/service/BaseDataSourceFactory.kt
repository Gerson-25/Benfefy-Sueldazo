package com.syntepro.appbeneficiosbolivia.service

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.syntepro.appbeneficiosbolivia.entity.service.BaseRequest
import com.syntepro.appbeneficiosbolivia.entity.service.BaseResponse
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response

class BaseDataSourceFactory<T>(private val scope: CoroutineScope, private val request: BaseRequest,
                               val service: suspend (BaseRequest) -> Response<BaseResponse<T>>) : DataSource.Factory<Long, T>() {

    val dsLiveData = MutableLiveData<BaseDataSource<T>>()

    override fun create(): DataSource<Long, T> {
        val newsDataSource = BaseDataSource(scope, request, service)
        dsLiveData.postValue(newsDataSource)
        return newsDataSource
    }
}