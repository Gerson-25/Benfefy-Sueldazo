package com.appbenefy.sueldazo.accounts.usecase

import com.appbenefy.sueldazo.core.entities.BaseRequest
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.accounts.model.AccountsRepository
import javax.inject.Inject

class DoCustomerInfo
@Inject constructor(private val accountsRepository: AccountsRepository) : UseCase<BaseResponse<Nothing>, DoCustomerInfo.Params>() {

    override suspend fun run(params: Params) = accountsRepository.getCustomer(params.token, params.request)

    data class Params(val token: String, val request: BaseRequest<Nothing>)
}
