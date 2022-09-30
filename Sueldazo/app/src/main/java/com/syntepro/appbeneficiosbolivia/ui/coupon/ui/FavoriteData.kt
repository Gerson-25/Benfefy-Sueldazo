package com.syntepro.appbeneficiosbolivia.ui.coupon.ui

import android.util.Log
import com.syntepro.appbeneficiosbolivia.entity.service.AddFavoriteRequest
import com.syntepro.appbeneficiosbolivia.service.NetworkService2
import com.syntepro.appbeneficiosbolivia.service.RetrofitClientInstance
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object FavoriteData {
    fun addFavorite(idCoupon: String?, completion: (String, Boolean) -> Unit) {
        val request = with(AddFavoriteRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = Functions.getLanguage()
            idUser = Constants.userProfile?.idUser
            idCupon = idCoupon
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.addFavorite(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            completion(ret.description ?: "Ocurrió un error", ret.data)
                        } else {
                            completion(ret.description ?: "Ocurrió un error", false)
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        completion(response.message() ?: "Ocurrió un error", false)
                        Log.e("Error", response.message())
                    }
                }
            } catch (e: Exception) {
                completion(e.message ?: "Ocurrió un error", false)
                Log.e("Exception", e.message?:e.cause?.message?:e.cause.toString())
            }
        }
    }

    fun removeFavorite(idCoupon: String?, completion: (String, Boolean) -> Unit) {
        val request = with(AddFavoriteRequest()) {
            country = Constants.userProfile?.actualCountry ?: "BO"
            language = Functions.getLanguage()
            idUser = Constants.userProfile?.idUser
            idCupon = idCoupon
            this
        }
        val job = Job()
        val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
        val apiService = RetrofitClientInstance.getClient(Constants.BASE_URL_MICRO2).create(NetworkService2::class.java)
        scopeMainThread.launch {
            try {
                val response = apiService.removeFavorite(request)
                when {
                    response.isSuccessful -> {
                        val ret = response.body()!!
                        if (ret.isSuccess) {
                            completion(ret.description ?: "Ocurrió un error", ret.data)
                        } else {
                            completion(ret.description ?: "Ocurrió un error", false)
                            Log.e("Error", "${ret.code}")
                        }
                    }
                    else -> {
                        completion(response.message() ?: "Ocurrió un error", false)
                        Log.e("Error", response.message())
                    }
                }
            } catch (e: Exception) {
                completion(e.message ?: "Ocurrió un error", false)
                Log.e("Exception", e.message?:e.cause?.message?:e.cause.toString())
            }
        }
    }
}