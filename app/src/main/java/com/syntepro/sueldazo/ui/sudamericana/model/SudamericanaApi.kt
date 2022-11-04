package com.syntepro.sueldazo.ui.sudamericana.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SudamericanaApi {
    companion object {
        private const val SURVEY = ""
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(SURVEY)
    fun saveSurvey(
            @Body body: SurveyRequest
    ): Call<BaseResponse<Boolean>>
}