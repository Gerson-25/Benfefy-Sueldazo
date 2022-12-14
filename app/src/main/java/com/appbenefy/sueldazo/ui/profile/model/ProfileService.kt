package com.appbenefy.sueldazo.ui.profile.model

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.home.model.UserSavingsRequest
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileService
@Inject constructor(retrofit: Retrofit) : ProfileApi {
    private val profileApiApi by lazy { retrofit.create(ProfileApi::class.java) }

    override fun getUserStats(body: UserStatsRequest): Call<BaseResponse<UserStatsResponse>> = profileApiApi.getUserStats(body)

    override fun getUserTransactions(body: TransactionRequest): Call<BaseResponse<SavingDetailsResponse>> = profileApiApi.getUserTransactions(body)

    override fun getTransactionDetail(body: TransactionDetailRequest): Call<BaseResponse<TransactionDetailResponse>> = profileApiApi.getTransactionDetail(body)

    override fun getSavingsResume(body: TransactionRequest): Call<BaseResponse<SavingResumeResponse>> = profileApiApi.getSavingsResume(body)

}