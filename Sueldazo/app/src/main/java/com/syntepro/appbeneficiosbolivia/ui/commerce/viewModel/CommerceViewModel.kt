package com.syntepro.appbeneficiosbolivia.ui.commerce.viewModel

import androidx.lifecycle.MutableLiveData
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.*
import com.syntepro.appbeneficiosbolivia.ui.commerce.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommerceViewModel
@Inject constructor(
        private val doBranch: DoBranch,
        private val doCommerce: DoCommerce,
        private val doCommerceFilter: DoCommerceFilter,
        private val doCommerceDetail: DoCommerceDetail
): BaseViewModel() {

    val branch: MutableLiveData<BaseResponse<List<BranchResponse>>> = MutableLiveData()
    val commerceByBranch: MutableLiveData<BaseResponse<List<CommerceByBranchResponse>>> = MutableLiveData()
    val filteredCommerce: MutableLiveData<BaseResponse<List<CommerceFilterResponse>>> = MutableLiveData()
    val commerceDetail: MutableLiveData<BaseResponse<CommerceDetailResponse>> = MutableLiveData()

    fun getBranch(request: BranchRequest) =
            doBranch(DoBranch.Params(request)) {
                it.fold(::handleFailure, ::handleBranch)
            }

    fun getCommerceByBranch(request: CommerceByBranchRequest) =
            doCommerce(DoCommerce.Params(request)) {
                it.fold(::handleFailure, ::handleCommerce)
            }

    fun getFilteredCommerce(request: CommerceFilterRequest) =
            doCommerceFilter(DoCommerceFilter.Params(request)) {
                it.fold(::handleFailure, ::handleFilterCommerce)
            }

    fun getCommerceDetail(request: CommerceDetailRequest) =
            doCommerceDetail(DoCommerceDetail.Params(request)) {
                it.fold(::handleFailure, ::handleCommerceDetail)
            }

    private fun handleBranch(response: BaseResponse<List<BranchResponse>>) {
        this.branch.value = response
    }

    private fun handleCommerce(response: BaseResponse<List<CommerceByBranchResponse>>) {
        this.commerceByBranch.value = response
    }

    private fun handleFilterCommerce(response: BaseResponse<List<CommerceFilterResponse>>) {
        this.filteredCommerce.value = response
    }

    private fun handleCommerceDetail(response: BaseResponse<CommerceDetailResponse>) {
        this.commerceDetail.value = response
    }

}