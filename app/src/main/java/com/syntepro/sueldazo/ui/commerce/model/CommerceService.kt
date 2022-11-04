package com.syntepro.sueldazo.ui.commerce.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommerceService
@Inject constructor(retrofit: Retrofit) : CommerceApi {
    private val commerceApiApi by lazy { retrofit.create(CommerceApi::class.java) }

    override fun getBranch(body: BranchRequest): Call<BaseResponse<List<BranchResponse>>> = commerceApiApi.getBranch(body)
    override fun getCommerceByBranch(body: CommerceByBranchRequest): Call<BaseResponse<List<CommerceByBranchResponse>>> = commerceApiApi.getCommerceByBranch(body)
    override fun getFilteredCommerce(body: CommerceFilterRequest): Call<BaseResponse<List<CommerceFilterResponse>>> = commerceApiApi.getFilteredCommerce(body)
    override fun getCommerceDetail(body: CommerceDetailRequest): Call<BaseResponse<CommerceDetailResponse>> = commerceApiApi.getCommerceDetail(body)
    override fun getCommerceAgency(body: CommerceAgencyRequest): Call<BaseResponse<List<AgencyResponse>>> = commerceApiApi.getCommerceAgency(body)
}