package com.syntepro.sueldazo.accounts.viewModel

import androidx.lifecycle.MutableLiveData
import com.syntepro.sueldazo.core.entities.BaseRequest
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.merckers.core.platform.BaseViewModel
import com.syntepro.sueldazo.accounts.usecase.DoCustomerInfo
import javax.inject.Inject

class AccountsViewModel
@Inject constructor(
    private val doAccounts: DoCustomerInfo
) : BaseViewModel() {

    var accounts: MutableLiveData<BaseResponse<Nothing>> = MutableLiveData()

    fun accountsDetail(token: String, request: BaseRequest<Nothing>) =
        doAccounts(DoCustomerInfo.Params(token, request)) {
            it.fold(::handleFailure, ::handleAccounts)
        }

    private fun handleAccounts(response: BaseResponse<Nothing>) {
        this.accounts.value = response
    }

}