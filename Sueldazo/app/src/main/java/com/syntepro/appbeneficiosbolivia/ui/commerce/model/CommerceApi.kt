package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ApiConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CommerceApi {
    companion object {
        private const val BRANCH = "BusinessLine/GetActiveBranch"
        private const val COMMERCE_BY_BRANCH = "Commerce/GetCommerceByBranch"
        private const val COMMERCE_FILTER = "Commerce/GetCommerceFiltered"
        private const val COMMERCE_DETAIL = "Commerce/GetCommerceDetail"
        private const val COMMERCE_AGENCY = "Commerce/GetCommerceAgencies"
    }

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(BRANCH)
    fun getBranch(
            @Body body: BranchRequest
    ): Call<BaseResponse<List<BranchResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COMMERCE_BY_BRANCH)
    fun getCommerceByBranch(
            @Body body: CommerceByBranchRequest
    ): Call<BaseResponse<List<CommerceByBranchResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COMMERCE_FILTER)
    fun getFilteredCommerce(
            @Body body: CommerceFilterRequest
    ): Call<BaseResponse<List<CommerceFilterResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COMMERCE_DETAIL)
    fun getCommerceDetail(
            @Body body: CommerceDetailRequest
    ): Call<BaseResponse<CommerceDetailResponse>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(COMMERCE_AGENCY)
    fun getCommerceAgency(
            @Body body: CommerceAgencyRequest
    ): Call<BaseResponse<List<AgencyResponse>>>
}