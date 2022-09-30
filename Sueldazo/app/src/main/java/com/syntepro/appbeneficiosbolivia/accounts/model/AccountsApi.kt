package com.syntepro.appbeneficiosbolivia.accounts.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseRequest
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AccountsApi {
    companion object {
        private const val LOGIN_METHOD = "Procesos"
    }

    @Headers("Content-Type: application/json")
    @POST(LOGIN_METHOD)
    fun getCustomer(
        @Header("Authorization") token: String,
        @Body body: BaseRequest<Nothing>
    ): Call<BaseResponse<Nothing>>
}