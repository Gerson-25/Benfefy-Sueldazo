package com.syntepro.sueldazo.ui.agency.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AgencyApi {
    companion object {
        private const val AGENCY_DETAIL = "Agency/GetAgencyDetail"
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(AGENCY_DETAIL)
    fun getAgencyDetail(
            @Body body: AgencyDetailRequest
    ): Call<BaseResponse<AgencyDetailResponse>>
}