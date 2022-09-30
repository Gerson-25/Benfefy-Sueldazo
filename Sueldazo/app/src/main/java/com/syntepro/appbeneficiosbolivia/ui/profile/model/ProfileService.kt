package com.syntepro.appbeneficiosbolivia.ui.profile.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileService
@Inject constructor(retrofit: Retrofit) : ProfileApi {
    private val profileApiApi by lazy { retrofit.create(ProfileApi::class.java) }

    override fun getUserStats(body: UserStatsRequest): Call<BaseResponse<UserStatsResponse>> = profileApiApi.getUserStats(body)

    override fun getUserTransactions(body: TransactionRequest): Call<BaseResponse<List<TransactionResponse>>> = profileApiApi.getUserTransactions(body)

    override fun getTransactionDetail(body: TransactionDetailRequest): Call<BaseResponse<TransactionDetailResponse>> = profileApiApi.getTransactionDetail(body)

}