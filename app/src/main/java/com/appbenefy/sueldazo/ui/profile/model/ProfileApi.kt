package com.appbenefy.sueldazo.ui.profile.model

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProfileApi {
    companion object {
        private const val USER_STATS = "User/GetUserStats"
        private const val USER_TRANSACTION = "User/GetUserSavingDetails"
        private const val TRANSACTION_DETAIL = "Transaction/GetTransactionDetail"
        private const val SAVINGS_RESUME = "User/GetUserSavingHeader"
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(USER_STATS)
    fun getUserStats(
            @Body body: UserStatsRequest
    ): Call<BaseResponse<UserStatsResponse>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(USER_TRANSACTION)
    fun getUserTransactions(
            @Body body: TransactionRequest
    ): Call<BaseResponse<SavingDetailsResponse>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(TRANSACTION_DETAIL)
    fun getTransactionDetail(
            @Body body: TransactionDetailRequest
    ): Call<BaseResponse<TransactionDetailResponse>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(SAVINGS_RESUME)
    fun getSavingsResume(
        @Body body: TransactionRequest
    ): Call<BaseResponse<SavingResumeResponse>>
}