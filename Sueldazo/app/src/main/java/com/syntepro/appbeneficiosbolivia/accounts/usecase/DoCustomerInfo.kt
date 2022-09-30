package com.syntepro.appbeneficiosbolivia.accounts.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseRequest
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.accounts.model.AccountsRepository
import javax.inject.Inject

class DoCustomerInfo
@Inject constructor(private val accountsRepository: AccountsRepository) : UseCase<BaseResponse<Nothing>, DoCustomerInfo.Params>() {

    override suspend fun run(params: Params) = accountsRepository.getCustomer(params.token, params.request)

    data class Params(val token: String, val request: BaseRequest<Nothing>)
}
