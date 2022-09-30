package com.syntepro.appbeneficiosbolivia.service

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.syntepro.appbeneficiosbolivia.entity.service.BaseRequest
import com.syntepro.appbeneficiosbolivia.entity.service.BaseResponse
import io.reactivex.Completable
import io.reactivex.functions.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class BaseDataSource<T>(private val scope: CoroutineScope, private val request: BaseRequest,
                        val service: suspend (BaseRequest) -> Response<BaseResponse<T>>) : PageKeyedDataSource<Long, T>() {
    private val serviceStatus = ServiceStatus()
    private var retryCompletable: Completable? = null
    var status: MutableLiveData<ServiceStatus> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, T>) {
        updateState(State.LOADING)
        scope.launch {
            try {
                val response = service(request)
                when{
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if(ret.isSuccess) {
                            if(ret.data!!.isNotEmpty()) {
                                updateState(State.DONE)
                                callback.onResult(ret.data!!, null, 2)
                            } else updateState(State.EMPTY)
                        }
                        else updateStatusError(ret.description, ServiceStatus.ERROR_TYPE_SERVICE)
                    }
                    else -> {
                        updateStatusError(response.message(), ServiceStatus.ERROR_TYPE_NETWORK)
                    }
                }
            }catch (exception : Exception){
                updateStatusError(exception.message?:exception.cause?.message?:exception.cause.toString(),
                    ServiceStatus.ERROR_TYPE_NETWORK
                )
                setRetry { loadInitial(params, callback) }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, T>) {
        updateState(State.LOADING)
        request.pageNumber = params.key
        scope.launch {
            try {
                val response = service(request)
                when{
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if(ret.isSuccess) {
                            updateState(State.DONE)
                            callback.onResult(ret.data!!, (params.key + 1))
                        }
                        else {
                            updateStatusError(ret.description,
                                ServiceStatus.ERROR_TYPE_SERVICE
                            )
                        }
                    }
                    else -> {
                        updateStatusError(response.message(),
                            ServiceStatus.ERROR_TYPE_NETWORK
                        )
                    }
                }
            }catch (exception : Exception){
                updateStatusError(exception.cause?.message,
                    ServiceStatus.ERROR_TYPE_NETWORK
                )
                setRetry { loadAfter(params, callback) }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, T>) {

    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    private fun updateState(state: State) {
        this.serviceStatus.status = state.ordinal
        GlobalScope.launch(Dispatchers.Main) {
            status.value = serviceStatus
        }
    }

    private fun updateStatusError(message: String?, errorType: Int) {
        serviceStatus.message = message
        serviceStatus.state = State.ERROR
        serviceStatus.status = ServiceStatus.STATUS_ERROR
        serviceStatus.type = errorType
        GlobalScope.launch(Dispatchers.Main) {
            status.value = serviceStatus
        }
    }

}