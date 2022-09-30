package com.syntepro.appbeneficiosbolivia.accounts.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseRequest
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsService
@Inject constructor(retrofit: Retrofit) : AccountsApi {
    private val accountsApi by lazy { retrofit.create(AccountsApi::class.java) }

    override fun getCustomer(
        token: String,
        body: BaseRequest<Nothing>
    ): Call<BaseResponse<Nothing>>  = accountsApi.getCustomer(token, body)
}


