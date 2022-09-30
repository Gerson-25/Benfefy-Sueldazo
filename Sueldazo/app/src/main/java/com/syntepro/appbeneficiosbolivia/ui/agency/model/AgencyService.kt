package com.syntepro.appbeneficiosbolivia.ui.agency.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject

class AgencyService
@Inject constructor(retrofit: Retrofit) : AgencyApi {
    private val agencyApiApi by lazy { retrofit.create(AgencyApi::class.java) }

    override fun getAgencyDetail(body: AgencyDetailRequest): Call<BaseResponse<AgencyDetailResponse>> = agencyApiApi.getAgencyDetail(body)

}