package com.appbenefy.sueldazo.ui.home.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.entity.service.CategoryRequest
import com.appbenefy.sueldazo.service.NetworkService2
import com.appbenefy.sueldazo.service.RetrofitClientInstance
import com.appbenefy.sueldazo.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CategoryRepository {
    fun getCategories(request: CategoryRequest): LiveData<MutableList<Category>?> {
        val mutableData = MutableLiveData<MutableList<Category>?>()

        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.getCategory(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            ret.data?.let {
                                mutableData.value = it
                            }
                        } else mutableData.value = null
                    }
                    else -> { mutableData.value = null }
                }
            } catch (e: Exception) { mutableData.value = null }
        }

        return mutableData
    }
}