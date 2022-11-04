package com.syntepro.sueldazo.ui.sudamericana.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudamericanaService
@Inject constructor(retrofit: Retrofit) : SudamericanaApi {
    private val sudamericanaApiApi by lazy { retrofit.create(SudamericanaApi::class.java) }

    override fun saveSurvey(body: SurveyRequest): Call<BaseResponse<Boolean>> = sudamericanaApiApi.saveSurvey(body)

}