package com.appbenefy.sueldazo.ui.commerce.model

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import com.appbenefy.sueldazo.ui.agency.model.AgencyResponse
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

    @Headers(CONTENT_TYPE_JSON)
    @POST(BRANCH)
    fun getBranch(
            @Body body: BranchRequest
    ): Call<BaseResponse<List<BranchResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(COMMERCE_BY_BRANCH)
    fun getCommerceByBranch(
            @Body body: CommerceByBranchRequest
    ): Call<BaseResponse<List<CommerceByBranchResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(COMMERCE_FILTER)
    fun getFilteredCommerce(
            @Body body: CommerceFilterRequest
    ): Call<BaseResponse<List<CommerceFilterResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(COMMERCE_DETAIL)
    fun getCommerceDetail(
            @Body body: CommerceDetailRequest
    ): Call<BaseResponse<CommerceDetailResponse>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(COMMERCE_AGENCY)
    fun getCommerceAgency(
            @Body body: CommerceAgencyRequest
    ): Call<BaseResponse<List<AgencyResponse>>>
}