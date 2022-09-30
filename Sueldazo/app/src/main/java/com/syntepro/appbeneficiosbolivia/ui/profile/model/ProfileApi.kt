package com.syntepro.appbeneficiosbolivia.ui.profile.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.lealtad.ApiConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ProfileApi {
    companion object {
        private const val USER_STATS = "User/GetUserStats"
        private const val USER_TRANSACTION = "Transaction/GetTransactionsList"
        private const val TRANSACTION_DETAIL = "Transaction/GetTransactionDetail"
    }

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(USER_STATS)
    fun getUserStats(
            @Body body: UserStatsRequest
    ): Call<BaseResponse<UserStatsResponse>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(USER_TRANSACTION)
    fun getUserTransactions(
            @Body body: TransactionRequest
    ): Call<BaseResponse<List<TransactionResponse>>>

    @Headers(ApiConfig.CONTENT_TYPE_JSON)
    @POST(TRANSACTION_DETAIL)
    fun getTransactionDetail(
            @Body body: TransactionDetailRequest
    ): Call<BaseResponse<TransactionDetailResponse>>
}